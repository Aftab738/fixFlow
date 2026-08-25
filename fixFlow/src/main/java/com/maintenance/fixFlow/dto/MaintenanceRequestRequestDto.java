package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.MaintenanceCategory;
import com.maintenance.fixFlow.entity.MaintenancePriority;
import com.maintenance.fixFlow.entity.MaintenanceStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MaintenanceRequestRequestDto {

    private String title;

    private String description;

    private MaintenanceStatus status;

    private MaintenancePriority priority;

    private MaintenanceCategory category;

    private Long reportedById;

    private Long unitId;
}