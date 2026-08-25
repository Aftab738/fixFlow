package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationRequestDto {

    private String message;

    private NotificationType type;

    private boolean read;

    private Long userId;
}