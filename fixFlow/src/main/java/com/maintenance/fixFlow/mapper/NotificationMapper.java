package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.NotificationRequestDto;
import com.maintenance.fixFlow.dto.NotificationResponseDto;
import com.maintenance.fixFlow.entity.Notification;
import com.maintenance.fixFlow.entity.User;

public class NotificationMapper {
    public static Notification toEntity(NotificationRequestDto dto, User user){
        Notification notification=new Notification();

        notification.setType(dto.getType());
        notification.setUser(user);
        notification.setMessage(dto.getMessage());
        notification.setRead(dto.isRead());

        return notification;
    }

    public static NotificationResponseDto toResponseDto(Notification notification){
        NotificationResponseDto dto=new NotificationResponseDto();

        dto.setRead(notification.isRead());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setId(notification.getId());

        if(notification.getUser()!=null){
            dto.setUserId(notification.getUser().getId());
        }

        return dto;
    }
}
