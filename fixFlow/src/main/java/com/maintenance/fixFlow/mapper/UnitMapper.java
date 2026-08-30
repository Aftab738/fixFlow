package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.UnitRequestDto;
import com.maintenance.fixFlow.dto.UnitResponseDto;
import com.maintenance.fixFlow.entity.Property;
import com.maintenance.fixFlow.entity.Unit;

public class UnitMapper {
    public static Unit toEntity(UnitRequestDto dto, Property property){
        Unit unit=new Unit();

        unit.setUnitNumber(dto.getUnitNumber());
        unit.setFloor(dto.getFloor());
        unit.setProperty(property);

        return unit;
    }

    public static UnitResponseDto toResponseDto(Unit unit){
        UnitResponseDto dto=new UnitResponseDto();

        dto.setId(unit.getId());
        dto.setFloor(unit.getFloor());
        dto.setUnitNumber(unit.getUnitNumber());

        if(unit.getProperty()!=null){
            dto.setPropertyId(unit.getProperty().getId());
            dto.setPropertyName(unit.getProperty().getName());
        }

        return dto;
    }
}
