package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.AssignmentRequestDto;
import com.maintenance.fixFlow.dto.AssignmentResponseDto;
import com.maintenance.fixFlow.dto.AssignmentUpdateDto;
import com.maintenance.fixFlow.dto.NotificationRequestDto;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.exception.BusinessException;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.AssignmentMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        if (maintenanceRequest.getStatus() == MaintenanceStatus.COMPLETED ||
                maintenanceRequest.getStatus() == MaintenanceStatus.CANCELLED ||
                maintenanceRequest.getStatus() == MaintenanceStatus.REJECTED) {

            throw new BusinessException("Cannot assign vendor to a "
                            + maintenanceRequest.getStatus()
                            + " maintenance request"
            );
        }


        List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(
                        maintenanceRequest.getId());

        for (Assignment a : assignments) {
            if (a.getStatus() == AssignmentStatus.PENDING ||
                    a.getStatus() == AssignmentStatus.ACCEPTED) {
                throw new BusinessException("Maintenance request already has an active assignment");
            }
        }


        User vendor = userRepository.findById(dto.getVendorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        if (vendor.getRole() != Role.VENDOR) {
            throw new BusinessException("Selected user is not a vendor");
        }

        Assignment assignment = AssignmentMapper.toEntity(
                        dto,
                        maintenanceRequest,
                        vendor);

        assignment.setStatus(AssignmentStatus.PENDING); // New assignments always start as PENDING
        assignment.setAssignedAt(LocalDateTime.now());

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
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Assignment not found with id: " + id
                                ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean vendor = false;
        boolean tenant = false;

        for(var a :authentication.getAuthorities()){
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            } else if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            } else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        if (manager) {
            return AssignmentMapper.toResponseDto(assignment);
        }

        if (vendor) {
            if (!email.equals(assignment.getVendor().getEmail())) {
                throw new AccessDeniedException(
                        "You are not allowed to view this assignment"
                );
            }
        }

        if (tenant) {
            if (!email.equals(assignment.getMaintenanceRequest().getReportedBy().getEmail())) {

                throw new AccessDeniedException(
                        "You are not allowed to view this assignment"
                );
            }
        }

        return AssignmentMapper.toResponseDto(assignment);
    }

    public List<AssignmentResponseDto> getAllAssignments() {

        List<Assignment> list = assignmentRepository.findAll();

        List<AssignmentResponseDto> res = new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    @Transactional
    public AssignmentResponseDto updateAssignment(
            AssignmentUpdateDto dto,
            Long id) {

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assignment not found with id: " + id
                        ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        boolean manager = false;
        boolean vendor = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }

            if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
        }

        if (!manager && !vendor) {
            throw new AccessDeniedException(
                    "You are not allowed to modify this assignment"
            );
        }

        if (vendor) { //if the user is a vendor,allow the update of Assignment if only if the assignment belongs to that user
            String vendorEmail = assignment.getVendor().getEmail();

            if (!email.equals(vendorEmail)) {
                throw new AccessDeniedException(
                        "You are not allowed to modify this assignment"
                );
            }
        }

        MaintenanceRequest maintenanceRequest =assignment.getMaintenanceRequest();

        //Checking Assignment status Transition before update.
        AssignmentStatus oldStatus = assignment.getStatus();
        AssignmentStatus newStatus = dto.getStatus();

        if ((newStatus == AssignmentStatus.ACCEPTED
                || newStatus == AssignmentStatus.REJECTED
                || newStatus == AssignmentStatus.COMPLETED)
                && !vendor) {

            throw new AccessDeniedException(
                    "Only the assigned vendor can update this assignment status"
            );
        }

        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new BusinessException(
                    "Invalid assignment status transition: "
                            + oldStatus + " to " + newStatus
            );
        }

        if (oldStatus == AssignmentStatus.PENDING
                && newStatus == AssignmentStatus.ACCEPTED) {

            assignment.setStatus(AssignmentStatus.ACCEPTED);
            assignment.setRespondedAt(LocalDateTime.now());
        }

        if (oldStatus == AssignmentStatus.PENDING
                && newStatus == AssignmentStatus.REJECTED) {

            assignment.setStatus(AssignmentStatus.REJECTED);
            assignment.setRespondedAt(LocalDateTime.now());
        }

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

        if (newStatus == AssignmentStatus.COMPLETED) {
            maintenanceRequest.setStatus(MaintenanceStatus.COMPLETED);
            maintenanceRequest.setCompletedAt(LocalDateTime.now());
        }

        if(newStatus==AssignmentStatus.REJECTED) maintenanceRequest.setStatus(MaintenanceStatus.REJECTED);
        if(newStatus==AssignmentStatus.CANCELLED) maintenanceRequest.setStatus(MaintenanceStatus.CANCELLED);

        maintenanceRequestRepository.save(maintenanceRequest);

        if (oldStatus == AssignmentStatus.ACCEPTED &&
                newStatus == AssignmentStatus.COMPLETED){ //sends notification to the tenant when its Maintenance request is completed
            NotificationRequestDto n=new NotificationRequestDto();

            n.setMessage("Your Maintenance request has been completed");
            n.setType(NotificationType.REQUEST_COMPLETED);
            n.setRead(false);
            n.setUserId(maintenanceRequest.getReportedBy().getId());

            notificationService.createNotification(n);
        }

        Assignment savedAssignment = assignmentRepository.save(assignment);

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

        MaintenanceRequest maintenanceRequest=maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Maintenance request not found")
                );

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        boolean vendor = false;
        boolean tenant=false;

        for(var a:authentication.getAuthorities()){
            if(a.getAuthority().equals("ROLE_VENDOR")){
                vendor=true;
            }
            else if(a.getAuthority().equals("ROLE_TENANT")){
                tenant=true;
            }
        }

        if(tenant){
            if(!maintenanceRequest.getReportedBy().getEmail().equals(email)){
                throw new AccessDeniedException("You are not allowed to view this assignment");
            }
        }

        List<Assignment> list = assignmentRepository.findByMaintenanceRequestId(maintenanceRequestId);

        if (vendor) {
            boolean allowed = false;
            for (Assignment assignment : list) {
                if (assignment.getVendor().getEmail().equals(email)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new AccessDeniedException(
                        "You are not allowed to view this assignment"
                );
            }
        }

        List<AssignmentResponseDto> res = new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByVendorId(
            Long vendorId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        boolean manager = false;
        boolean vendor = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
        }

        if (!manager && !vendor) {
            throw new AccessDeniedException(
                    "You are not allowed to view vendor assignments"
            );
        }

        if (vendor) {
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            if (!user.getId().equals(vendorId)) {
                throw new AccessDeniedException(
                        "You are not allowed to view these assignments"
                );
            }
        }


        List<Assignment> list = assignmentRepository.findByVendorId(vendorId);

        List<AssignmentResponseDto> res = new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByStatus(
            AssignmentStatus status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean vendor = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            } else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        if (tenant) {
            throw new AccessDeniedException(
                    "You are not allowed to view these assignments"
            );
        }

        List<Assignment> list = assignmentRepository.findByStatus(status);

        List<AssignmentResponseDto> res = new ArrayList<>();

        for (Assignment assignment : list) {
            if (vendor) {
                if (assignment.getVendor().getEmail().equals(email)) { //vendor gets only their assignment with that status
                    res.add(AssignmentMapper.toResponseDto(assignment));
                }
            } else {
                // Manager
                res.add(AssignmentMapper.toResponseDto(assignment));
            }
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByVendorIdAndStatus(Long vendorId, AssignmentStatus status) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean vendor = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
             if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
             else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        if (tenant) {
            throw new AccessDeniedException(
                    "You are not allowed to view these assignments"
            );
        }

        if (vendor) {
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            if (!user.getId().equals(vendorId)) {
                throw new AccessDeniedException(
                        "You are not allowed to view these assignments"
                );
            }
        }

        List<Assignment> list = assignmentRepository.findByVendorIdAndStatus(vendorId, status);

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