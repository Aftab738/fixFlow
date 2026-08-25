package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.AssignmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentResponseDto {

    private Long id;

    private LocalDateTime assignedAt;

    private LocalDateTime respondedAt;

    private AssignmentStatus status;

    private String notes;

    private Long maintenanceRequestId;

    private Long vendorId;

    private String vendorName;
}