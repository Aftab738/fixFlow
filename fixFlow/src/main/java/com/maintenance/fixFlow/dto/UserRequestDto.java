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

    @NotBlank(message = "Enter your name")
    @Size(min=3,max = 50)
    private String name;

    @NotBlank(message = "Enter an Email")
    @Email(message = "Invalid Email")
    private String email;

    @NotBlank(message = "Enter Phone no")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @NotNull(message = "Enter your role")
    private Role role;

    @NotNull(message = "Enter your UnitId")
    private Long unitId;
}
