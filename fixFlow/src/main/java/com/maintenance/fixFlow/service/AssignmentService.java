package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.entity.Assignment;
import com.maintenance.fixFlow.entity.AssignmentStatus;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id).orElse(null);
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Assignment updateAssignment(Assignment assignment, Long id) {

        Optional<Assignment> existing =
                assignmentRepository.findById(id);

        if (existing.isPresent()) {

            Assignment a = existing.get();

            a.setAssignedAt(assignment.getAssignedAt());
            a.setRespondedAt(assignment.getRespondedAt());
            a.setStatus(assignment.getStatus());
            a.setNotes(assignment.getNotes());
            a.setMaintenanceRequest(assignment.getMaintenanceRequest());
            a.setVendor(assignment.getVendor());

            return assignmentRepository.save(a);
        }

        return null;
    }

    public String deleteAssignment(Long id) {

        Optional<Assignment> assignment =
                assignmentRepository.findById(id);

        if (assignment.isPresent()) {
            assignmentRepository.deleteById(id);
            return "Assignment deleted";
        }

        return "Assignment not found";
    }

    public List<Assignment> getAssignmentsByMaintenanceRequestId(
            Long maintenanceRequestId) {

        return assignmentRepository
                .findByMaintenanceRequestId(maintenanceRequestId);
    }

    public List<Assignment> getAssignmentsByVendorId(Long vendorId) {

        return assignmentRepository.findByVendorId(vendorId);
    }

    public List<Assignment> getAssignmentsByStatus(
            AssignmentStatus status) {

        return assignmentRepository.findByStatus(status);
    }

    public List<Assignment> getAssignmentsByVendorIdAndStatus(
            Long vendorId,
            AssignmentStatus status) {

        return assignmentRepository
                .findByVendorIdAndStatus(vendorId, status);
    }
}