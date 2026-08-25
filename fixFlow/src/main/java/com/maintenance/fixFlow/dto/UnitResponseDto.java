package com.maintenance.fixFlow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnitResponseDto {

    private Long id;

    private Integer unitNumber;

    private Integer floor;

    private Long propertyId;

    private String propertyName;
}