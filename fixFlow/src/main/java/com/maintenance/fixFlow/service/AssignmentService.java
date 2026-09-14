package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.AssignmentRequestDto;
import com.maintenance.fixFlow.dto.AssignmentResponseDto;
import com.maintenance.fixFlow.dto.NotificationRequestDto;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.exception.BusinessException;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.AssignmentMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository, NotificationService notificationService) {

        this.assignmentRepository = assignmentRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }
    @Transactional
    public AssignmentResponseDto createAssignment(
            AssignmentRequestDto dto) {

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        User vendor = userRepository.findById(dto.getVendorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        Assignment assignment = AssignmentMapper.toEntity(
                        dto,
                        maintenanceRequest,
                        vendor);

        assignment.setStatus(AssignmentStatus.PENDING); // New assignments always start as PENDING

        Assignment savedAssignment = assignmentRepository.save(assignment);

        //changing the maintenanceRequest status to Assigned
        maintenanceRequest.setStatus(MaintenanceStatus.ASSIGNED);
        maintenanceRequestRepository.save(maintenanceRequest);

        //Automated Notification
        NotificationRequestDto notificationRequestDto=new NotificationRequestDto();
        notificationRequestDto.setMessage("A maintenance request has been assigned to you.");
        notificationRequestDto.setType(NotificationType.REQUEST_ASSIGNED);
        notificationRequestDto.setRead(false);
        notificationRequestDto.setUserId(vendor.getId());

        notificationService.createNotification(notificationRequestDto);

        return AssignmentMapper.toResponseDto(savedAssignment);
    }

    public AssignmentResponseDto getAssignmentById(Long id) {

        Assignment assignment = assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found with id: " + id
                                ));

        return AssignmentMapper.toResponseDto(assignment);
    }

    public List<AssignmentResponseDto> getAllAssignments() {

        List<Assignment> list =
                assignmentRepository.findAll();

        List<AssignmentResponseDto> res =
                new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }
    @Transactional
    public AssignmentResponseDto updateAssignment(
            AssignmentRequestDto dto,
            Long id) {

        Assignment assignment = assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found with id: " + id
                                ));

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        User vendor = userRepository.findById(dto.getVendorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        //Checking Assignment status Transition before update.
        AssignmentStatus oldStatus = assignment.getStatus();
        AssignmentStatus newStatus = dto.getStatus();

        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new BusinessException(
                    "Invalid assignment status transition: "
                            + oldStatus + " to " + newStatus
            );
        }

        assignment.setMaintenanceRequest(maintenanceRequest);
        assignment.setVendor(vendor);
        assignment.setAssignedAt(dto.getAssignedAt());
        assignment.setRespondedAt(dto.getRespondedAt());
        assignment.setNotes(dto.getNotes());

        assignment.setStatus(newStatus);

        // Notify tenant about vendor response
        if (oldStatus == AssignmentStatus.PENDING &&
                newStatus == AssignmentStatus.ACCEPTED) {

            NotificationRequestDto n = new NotificationRequestDto();
            n.setMessage("Your maintenance request has been accepted by the vendor.");
            n.setType(NotificationType.ASSIGNMENT_ACCEPTED);
            n.setRead(false);
            n.setUserId(maintenanceRequest.getReportedBy().getId());

            notificationService.createNotification(n);
        }

        if (oldStatus == AssignmentStatus.PENDING &&
                newStatus == AssignmentStatus.REJECTED) {

            NotificationRequestDto n = new NotificationRequestDto();
            n.setMessage("Your maintenance request has been rejected by the vendor.");
            n.setType(NotificationType.ASSIGNMENT_REJECTED);
            n.setRead(false);
            n.setUserId(maintenanceRequest.getReportedBy().getId());

            notificationService.createNotification(n);
        }

        //updating the maintenanceReq status according to the new Assignment Status
        if(newStatus==AssignmentStatus.ACCEPTED) maintenanceRequest.setStatus(MaintenanceStatus.IN_PROGRESS);
        if(newStatus==AssignmentStatus.COMPLETED) maintenanceRequest.setStatus(MaintenanceStatus.COMPLETED);
        if(newStatus==AssignmentStatus.REJECTED) maintenanceRequest.setStatus(MaintenanceStatus.REJECTED);
        if(newStatus==AssignmentStatus.CANCELLED) maintenanceRequest.setStatus(MaintenanceStatus.CANCELLED);

        maintenanceRequestRepository.save(maintenanceRequest);

        if(newStatus==AssignmentStatus.COMPLETED){ //sends notification to the tenant when its Maintenance request is completed
            NotificationRequestDto n=new NotificationRequestDto();

            n.setMessage("Your Maintenance request has been completed");
            n.setType(NotificationType.REQUEST_COMPLETED);
            n.setRead(false);
            n.setUserId(maintenanceRequest.getReportedBy().getId());

            notificationService.createNotification(n);
        }

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        return AssignmentMapper.toResponseDto(savedAssignment);
    }

    public String deleteAssignment(Long id) {

        Assignment assignment = assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found with id: " + id
                                ));

        assignmentRepository.delete(assignment);

        return "Assignment Deleted";
    }

    public List<AssignmentResponseDto> getAssignmentsByMaintenanceRequestId(
            Long maintenanceRequestId) {

        List<Assignment> list = assignmentRepository
                        .findByMaintenanceRequestId(maintenanceRequestId);

        List<AssignmentResponseDto> res = new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByVendorId(
            Long vendorId) {

        List<Assignment> list = assignmentRepository.findByVendorId(vendorId);

        List<AssignmentResponseDto> res = new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByStatus(
            AssignmentStatus status) {

        List<Assignment> list = assignmentRepository.findByStatus(status);

        List<AssignmentResponseDto> res = new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByVendorIdAndStatus(
            Long vendorId,
            AssignmentStatus status) {

        List<Assignment> list = assignmentRepository
                        .findByVendorIdAndStatus(vendorId, status);

        List<AssignmentResponseDto> res = new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    private boolean isValidStatusTransition(
            AssignmentStatus oldStatus,
            AssignmentStatus newStatus) {

        // Status not changed
        if (oldStatus == newStatus) return true;

        if (oldStatus == AssignmentStatus.PENDING && newStatus == AssignmentStatus.ACCEPTED) return true;

        if (oldStatus == AssignmentStatus.ACCEPTED && newStatus == AssignmentStatus.COMPLETED) return true;

        // Rejection
        if (oldStatus == AssignmentStatus.PENDING && newStatus == AssignmentStatus.REJECTED) return true;

        // Cancellation
        if (oldStatus == AssignmentStatus.PENDING && newStatus == AssignmentStatus.CANCELLED) return true;

        if (oldStatus == AssignmentStatus.ACCEPTED && newStatus == AssignmentStatus.CANCELLED) return true;

        return false;
    }
}