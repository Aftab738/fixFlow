package com.maintenance.fixFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank(message ="Message can not be empty")
    private String message;

    @NotNull(message = "Enter author id")
    private Long authorId;

    @NotNull(message = "Enter maintenance request id")
    private Long maintenanceRequestId;
}