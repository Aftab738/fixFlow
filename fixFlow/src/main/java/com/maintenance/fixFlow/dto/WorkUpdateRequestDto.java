package com.maintenance.fixFlow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkUpdateRequestDto {

    private String message;

    private Long maintenanceRequestId;

    private Long vendorId;
}