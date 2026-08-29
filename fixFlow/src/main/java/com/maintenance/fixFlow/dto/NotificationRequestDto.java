package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationRequestDto {
    @NotBlank(message ="Message can not be empty")
    private String message;

    @NotNull(message = "Enter the Notification type")
    private NotificationType type;

    private boolean read;

    @NotNull(message = "Enter User Id")
    private Long userId;
}