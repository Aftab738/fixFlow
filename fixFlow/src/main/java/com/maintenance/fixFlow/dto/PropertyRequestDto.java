package com.maintenance.fixFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PropertyRequestDto {
    @NotBlank(message = "Enter the name")
    @Size(min = 3,max = 50)
    private String name;

    @NotBlank(message = "Enter the address")
    private String address;
}
