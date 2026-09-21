package com.maintenance.fixFlow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Enter an email")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Enter the password")
    private String password;
}