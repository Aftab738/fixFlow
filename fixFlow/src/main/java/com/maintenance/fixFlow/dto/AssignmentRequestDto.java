package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.AssignmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentRequestDto {
    @NotNull(message = "Enter maintenance request id")
    private Long maintenanceRequestId;

    @NotNull(message = "Enter vendor id")
    private Long vendorId;

    private LocalDateTime assignedAt;

    private LocalDateTime respondedAt;

    @NotNull(message = "Enter the status")
    private AssignmentStatus status;

    @NotBlank(message = "Enter notes")
    private String notes;
}