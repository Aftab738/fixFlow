package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.PropertyRequestDto;
import com.maintenance.fixFlow.dto.PropertyResponseDto;
import com.maintenance.fixFlow.entity.Property;

public class PropertyMapper {
    public static Property toEntity(PropertyRequestDto dto){
        Property property=new Property();

        property.setName(dto.getName());
        property.setAddress(dto.getAddress());

        return property;
    }

    public static PropertyResponseDto toResponseDto(Property property){
        PropertyResponseDto dto=new PropertyResponseDto();

        dto.setName(property.getName());
        dto.setId(property.getId());
        dto.setAddress(property.getAddress());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());

        return dto;
    }
}
