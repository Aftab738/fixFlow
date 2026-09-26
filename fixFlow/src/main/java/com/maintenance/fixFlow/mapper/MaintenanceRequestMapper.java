package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.MaintenanceRequestAdminCreateDto;
import com.maintenance.fixFlow.dto.MaintenanceRequestCreateDto;
import com.maintenance.fixFlow.dto.MaintenanceRequestResponseDto;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.MaintenanceStatus;
import com.maintenance.fixFlow.entity.Unit;
import com.maintenance.fixFlow.entity.User;

public class MaintenanceRequestMapper {

    public static MaintenanceRequest toEntity(
            MaintenanceRequestCreateDto dto,
            User reportedBy,
            Unit unit) {

        MaintenanceRequest request = new MaintenanceRequest();

        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setStatus(MaintenanceStatus.SUBMITTED);
        request.setPriority(dto.getPriority());
        request.setCategory(dto.getCategory());
        request.setReportedBy(reportedBy);
        request.setUnit(unit);

        return request;
    }

    public static MaintenanceRequest toEntity(
            MaintenanceRequestAdminCreateDto dto,
            User reportedBy,
            Unit unit) {

        MaintenanceRequest request = new MaintenanceRequest();

        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setStatus(MaintenanceStatus.SUBMITTED);
        request.setPriority(dto.getPriority());
        request.setCategory(dto.getCategory());
        request.setReportedBy(reportedBy);
        request.setUnit(unit);

        return request;
    }

    public static MaintenanceRequestResponseDto toResponseDto(
            MaintenanceRequest request) {

        MaintenanceRequestResponseDto dto =
                new MaintenanceRequestResponseDto();

        dto.setId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        dto.setPriority(request.getPriority());
        dto.setCategory(request.getCategory());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        dto.setCompletedAt(request.getCompletedAt());

        if (request.getReportedBy() != null) {
            dto.setReportedById(request.getReportedBy().getId());
            dto.setReportedByName(request.getReportedBy().getName());
        }

        if (request.getUnit() != null) {
            dto.setUnitId(request.getUnit().getId());
            dto.setUnitNumber(request.getUnit().getUnitNumber());
        }

        return dto;
    }
}