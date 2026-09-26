package com.maintenance.fixFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDto {

    @NotBlank(message = "Enter your name")
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank(message = "Enter Phone no")
    @Size(min = 10, max = 15,
            message = "Phone number must be between 10 and 15 digits")
    private String phone;
}