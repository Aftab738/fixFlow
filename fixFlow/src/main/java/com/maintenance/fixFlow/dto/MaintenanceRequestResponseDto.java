package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.MaintenanceCategory;
import com.maintenance.fixFlow.entity.MaintenancePriority;
import com.maintenance.fixFlow.entity.MaintenanceStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MaintenanceRequestResponseDto {

    private Long id;

    private String title;

    private String description;

    private MaintenanceStatus status;

    private MaintenancePriority priority;

    private MaintenanceCategory category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    private Long reportedById;

    private String reportedByName;

    private Long unitId;

    private Integer unitNumber;
}