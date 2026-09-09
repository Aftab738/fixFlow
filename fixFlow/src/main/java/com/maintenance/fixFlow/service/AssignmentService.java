package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.AssignmentRequestDto;
import com.maintenance.fixFlow.dto.AssignmentResponseDto;
import com.maintenance.fixFlow.entity.Assignment;
import com.maintenance.fixFlow.entity.AssignmentStatus;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.AssignmentMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository) {

        this.assignmentRepository = assignmentRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
    }

    public AssignmentResponseDto createAssignment(
            AssignmentRequestDto dto) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        User vendor =
                userRepository
                        .findById(dto.getVendorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        Assignment assignment =
                AssignmentMapper.toEntity(
                        dto,
                        maintenanceRequest,
                        vendor);

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        return AssignmentMapper.toResponseDto(savedAssignment);
    }

    public AssignmentResponseDto getAssignmentById(Long id) {

        Assignment assignment =
                assignmentRepository.findById(id)
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

    public AssignmentResponseDto updateAssignment(
            AssignmentRequestDto dto,
            Long id) {

        Assignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found with id: " + id
                                ));

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        User vendor =
                userRepository
                        .findById(dto.getVendorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        assignment.setMaintenanceRequest(maintenanceRequest);
        assignment.setVendor(vendor);
        assignment.setAssignedAt(dto.getAssignedAt());
        assignment.setRespondedAt(dto.getRespondedAt());
        assignment.setStatus(dto.getStatus());
        assignment.setNotes(dto.getNotes());

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        return AssignmentMapper.toResponseDto(savedAssignment);
    }

    public String deleteAssignment(Long id) {

        Assignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found with id: " + id
                                ));

        assignmentRepository.delete(assignment);

        return "Assignment Deleted";
    }

    public List<AssignmentResponseDto> getAssignmentsByMaintenanceRequestId(
            Long maintenanceRequestId) {

        List<Assignment> list =
                assignmentRepository
                        .findByMaintenanceRequestId(maintenanceRequestId);

        List<AssignmentResponseDto> res =
                new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByVendorId(
            Long vendorId) {

        List<Assignment> list =
                assignmentRepository.findByVendorId(vendorId);

        List<AssignmentResponseDto> res =
                new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByStatus(
            AssignmentStatus status) {

        List<Assignment> list =
                assignmentRepository.findByStatus(status);

        List<AssignmentResponseDto> res =
                new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }

    public List<AssignmentResponseDto> getAssignmentsByVendorIdAndStatus(
            Long vendorId,
            AssignmentStatus status) {

        List<Assignment> list =
                assignmentRepository
                        .findByVendorIdAndStatus(vendorId, status);

        List<AssignmentResponseDto> res =
                new ArrayList<>();

        for (Assignment assignment : list) {
            res.add(AssignmentMapper.toResponseDto(assignment));
        }

        return res;
    }
}