package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.AssignmentRequestDto;
import com.maintenance.fixFlow.dto.AssignmentResponseDto;
import com.maintenance.fixFlow.entity.Assignment;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.User;

public class AssignmentMapper {

    public static Assignment toEntity(
            AssignmentRequestDto dto,
            MaintenanceRequest maintenanceRequest,
            User vendor) {

        Assignment assignment = new Assignment();

        assignment.setMaintenanceRequest(maintenanceRequest);
        assignment.setVendor(vendor);
        assignment.setAssignedAt(dto.getAssignedAt());
        assignment.setRespondedAt(dto.getRespondedAt());
        assignment.setStatus(dto.getStatus());
        assignment.setNotes(dto.getNotes());

        return assignment;
    }

    public static AssignmentResponseDto toResponseDto(
            Assignment assignment) {

        AssignmentResponseDto dto =
                new AssignmentResponseDto();

        dto.setId(assignment.getId());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setRespondedAt(assignment.getRespondedAt());
        dto.setStatus(assignment.getStatus());
        dto.setNotes(assignment.getNotes());

        if (assignment.getMaintenanceRequest() != null) {
            dto.setMaintenanceRequestId(
                    assignment.getMaintenanceRequest().getId()
            );
        }

        if (assignment.getVendor() != null) {
            dto.setVendorId(assignment.getVendor().getId());
            dto.setVendorName(assignment.getVendor().getName());
        }

        return dto;
    }
}