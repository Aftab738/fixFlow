package com.maintenance.fixFlow.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RatingRequestDto {
    @NotNull(message = "Enter rating score")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer score;

    @NotBlank(message = "Enter the comment")
    private String comment;

    @NotNull(message = "Enter the maintenance request id")
    private Long maintenanceRequestId;

    @NotNull(message = "Enter the vendor id")
    private Long vendorId;

    @NotNull(message = "Enter the tenant id")
    private Long tenantId;
}