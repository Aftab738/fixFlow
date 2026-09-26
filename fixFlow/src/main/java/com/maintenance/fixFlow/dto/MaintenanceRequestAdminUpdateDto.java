package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.MaintenanceCategory;
import com.maintenance.fixFlow.entity.MaintenancePriority;
import com.maintenance.fixFlow.entity.MaintenanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MaintenanceRequestAdminUpdateDto {

    @NotBlank(message = "Enter title")
    private String title;

    @NotBlank(message = "Enter the description")
    private String description;

    @NotNull(message = "Enter status")
    private MaintenanceStatus status;

    @NotNull(message = "Enter priority")
    private MaintenancePriority priority;

    @NotNull(message = "Enter the category")
    private MaintenanceCategory category;

    @NotNull(message = "Enter reportedBy Id")
    private Long reportedById;

    @NotNull(message = "Enter unit Id")
    private Long unitId;
}