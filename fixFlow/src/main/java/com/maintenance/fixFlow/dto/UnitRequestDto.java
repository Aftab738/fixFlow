package com.maintenance.fixFlow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnitRequestDto {
    @NotNull(message = "Enter your Unit no.")
    private Integer unitNumber;

    @NotNull(message = "Enter your floor")
    private Integer floor;

    @NotNull(message = "Enter the property id")
    private Long propertyId;
}