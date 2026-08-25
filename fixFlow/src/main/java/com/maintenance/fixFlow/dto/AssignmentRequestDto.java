package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.AssignmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentRequestDto {

    private Long maintenanceRequestId;

    private Long vendorId;

    private LocalDateTime assignedAt;

    private LocalDateTime respondedAt;

    private AssignmentStatus status;

    private String notes;
}