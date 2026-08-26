package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank
    @Size(min=3,max = 50)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size
    private String phone;

    @NotNull
    private Role role;

    @NotNull
    private Long unitId;
}
