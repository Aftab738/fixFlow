package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.AssignmentRequestDto;
import com.maintenance.fixFlow.dto.AssignmentResponseDto;
import com.maintenance.fixFlow.entity.AssignmentStatus;
import com.maintenance.fixFlow.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public AssignmentResponseDto create(
            @Valid @RequestBody AssignmentRequestDto dto) {
        return assignmentService.createAssignment(dto);
    }

    @GetMapping("/{id}")
    public AssignmentResponseDto getById(@PathVariable Long id) {
        return assignmentService.getAssignmentById(id);
    }

    @GetMapping("/getAll")
    public List<AssignmentResponseDto> getAll() {
        return assignmentService.getAllAssignments();
    }

    @PutMapping("/{id}")
    public AssignmentResponseDto update(
            @Valid @RequestBody AssignmentRequestDto dto,
            @PathVariable Long id) {
        return assignmentService.updateAssignment(dto, id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        return assignmentService.deleteAssignment(id);
    }

    @GetMapping("/maintenanceRequestId/{maintenanceRequestId}")
    public List<AssignmentResponseDto> getByMaintenanceRequestId(
            @PathVariable Long maintenanceRequestId) {
        return assignmentService.getAssignmentsByMaintenanceRequestId(maintenanceRequestId);
    }

    @GetMapping("/vendorId/{vendorId}")
    public List<AssignmentResponseDto> getByVendorId(
            @PathVariable Long vendorId) {
        return assignmentService.getAssignmentsByVendorId(vendorId);
    }

    @GetMapping("/status/{status}")
    public List<AssignmentResponseDto> getByStatus(
            @PathVariable AssignmentStatus status) {
        return assignmentService.getAssignmentsByStatus(status);
    }

    @GetMapping("/vendorId/{vendorId}/status/{status}")
    public List<AssignmentResponseDto> getByVendorIdAndStatus(
            @PathVariable Long vendorId,
            @PathVariable AssignmentStatus status) {
        return assignmentService.getAssignmentsByVendorIdAndStatus(vendorId, status);
    }
}