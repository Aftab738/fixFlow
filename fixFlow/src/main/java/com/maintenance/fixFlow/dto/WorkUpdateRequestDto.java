package com.maintenance.fixFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkUpdateRequestDto {
    @NotBlank(message = "Message can not be empty")
    private String message;

    @NotNull(message = "Enter the maintenance request id")
    private Long maintenanceRequestId;

    @NotNull(message = "Enter the vendor Id")
    private Long vendorId;
}