package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.AssignmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class AssignmentUpdateDto {

    private LocalDateTime assignedAt;

    private LocalDateTime respondedAt;

    @NotNull(message = "Enter the current status")
    private AssignmentStatus status;

    @NotBlank(message = "Enter notes")
    private String notes;
}
