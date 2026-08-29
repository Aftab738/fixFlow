package com.maintenance.fixFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentRequestDto {
    @NotBlank(message = "Enter your name")
    @Size(min = 3,max = 50)
    private String name;

    @NotBlank(message = "Enter the content type")
    private String contentType;

    @NotNull(message = "Enter the size")
    @Positive
    private Long size;

    private String description;

    @NotNull(message = "Enter maintenance request id")
    private Long maintenanceRequestId;
}