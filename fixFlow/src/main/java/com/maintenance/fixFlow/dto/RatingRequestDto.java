package com.maintenance.fixFlow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RatingRequestDto {

    private Integer score;

    private String comment;

    private Long maintenanceRequestId;

    private Long vendorId;

    private Long tenantId;
}