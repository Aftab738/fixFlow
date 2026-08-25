package com.maintenance.fixFlow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RatingResponseDto {

    private Long id;

    private Integer score;

    private String comment;

    private LocalDateTime createdAt;

    private Long maintenanceRequestId;

    private Long vendorId;

    private String vendorName;

    private Long tenantId;

    private String tenantName;
}