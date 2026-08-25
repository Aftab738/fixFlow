package com.maintenance.fixFlow.dto;

import com.maintenance.fixFlow.entity.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponseDto {

    private Long id;

    private String message;

    private NotificationType type;

    private boolean read;

    private LocalDateTime createdAt;

    private Long userId;
}