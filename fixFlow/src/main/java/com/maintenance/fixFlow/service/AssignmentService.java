package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.AssignmentRequestDto;
import com.maintenance.fixFlow.dto.AssignmentResponseDto;
import com.maintenance.fixFlow.entity.Assignment;
import com.maintenance.fixFlow.entity.AssignmentStatus;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.mapper.AssignmentMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                        .orElse(null);

        User vendor =
                userRepository
                        .findById(dto.getVendorId())
                        .orElse(null);

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

        Optional<Assignment> assignment =
                assignmentRepository.findById(id);

        if (assignment.isPresent()) {
            return AssignmentMapper.toResponseDto(
                    assignment.get());
        }

        return null;
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

        Optional<Assignment> existingAssignment =
                assignmentRepository.findById(id);

        if (existingAssignment.isPresent()) {

            Assignment assignment =
                    existingAssignment.get();

            MaintenanceRequest maintenanceRequest =
                    maintenanceRequestRepository
                            .findById(dto.getMaintenanceRequestId())
                            .orElse(null);

            User vendor =
                    userRepository
                            .findById(dto.getVendorId())
                            .orElse(null);

            assignment.setMaintenanceRequest(maintenanceRequest);
            assignment.setVendor(vendor);
            assignment.setAssignedAt(dto.getAssignedAt());
            assignment.setRespondedAt(dto.getRespondedAt());
            assignment.setStatus(dto.getStatus());
            assignment.setNotes(dto.getNotes());

            Assignment savedAssignment =
                    assignmentRepository.save(assignment);

            return AssignmentMapper.toResponseDto(
                    savedAssignment);
        }

        return null;
    }

    public String deleteAssignment(Long id) {

        Optional<Assignment> assignment =
                assignmentRepository.findById(id);

        if (assignment.isPresent()) {
            assignmentRepository.delete(assignment.get());
            return "Assignment Deleted";
        }

        return "Assignment not found";
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