package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {

    private String name;

    private String email;

    private String phone;

    private Role role;

    private Long unitId;
}
