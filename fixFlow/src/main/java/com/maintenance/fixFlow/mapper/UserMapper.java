package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.UserRequestDto;
import com.maintenance.fixFlow.dto.UserResponseDto;
import com.maintenance.fixFlow.entity.Unit;
import com.maintenance.fixFlow.entity.User;

public class UserMapper {

    public static User toEntity(UserRequestDto dto, Unit unit){
        User user=new User();

        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setUnit(unit);
        user.setRole(dto.getRole());

        return user;
    }

    public static UserResponseDto toResponseDto(User user){
        UserResponseDto dto=new UserResponseDto();

        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setId(user.getId());


        if(user.getUnit()!=null){
            dto.setUnitId(user.getUnit().getId());
            dto.setUnitNumber(user.getUnit().getUnitNumber());

            if (user.getUnit().getProperty() != null) {
                dto.setPropertyId(user.getUnit().getProperty().getId());
                dto.setPropertyName(user.getUnit().getProperty().getName());
            }
        }
        return dto;
    }
}
