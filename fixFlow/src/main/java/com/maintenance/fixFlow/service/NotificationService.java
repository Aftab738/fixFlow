package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.NotificationRequestDto;
import com.maintenance.fixFlow.dto.NotificationResponseDto;
import com.maintenance.fixFlow.entity.Notification;
import com.maintenance.fixFlow.entity.NotificationType;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.NotificationMapper;
import com.maintenance.fixFlow.repository.NotificationRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public NotificationResponseDto createNotification(
            NotificationRequestDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + dto.getUserId()
                        ));

        Notification notification =
                NotificationMapper.toEntity(dto, user);

        Notification savedNotification =
                notificationRepository.save(notification);

        return NotificationMapper.toResponseDto(savedNotification);
    }

    public NotificationResponseDto getNotificationById(Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: " + id
                                ));

        return NotificationMapper.toResponseDto(notification);
    }

    public List<NotificationResponseDto> getAllNotifications() {

        List<Notification> list =
                notificationRepository.findAll();

        List<NotificationResponseDto> res =
                new ArrayList<>();

        for (Notification notification : list) {
            res.add(NotificationMapper.toResponseDto(notification));
        }

        return res;
    }

    public NotificationResponseDto updateNotification(
            NotificationRequestDto dto,
            Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: " + id
                                ));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + dto.getUserId()
                        ));

        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notification.setRead(dto.isRead());
        notification.setUser(user);

        Notification savedNotification =
                notificationRepository.save(notification);

        return NotificationMapper.toResponseDto(savedNotification);
    }

    public String deleteNotification(Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: " + id
                                ));

        notificationRepository.delete(notification);

        return "Notification Deleted";
    }

    public List<NotificationResponseDto> getNotificationsByUserId(
            Long userId) {

        List<Notification> list =
                notificationRepository.findByUserId(userId);

        List<NotificationResponseDto> res =
                new ArrayList<>();

        for (Notification notification : list) {
            res.add(NotificationMapper.toResponseDto(notification));
        }

        return res;
    }

    public List<NotificationResponseDto> getUnreadNotificationsByUserId(
            Long userId) {

        List<Notification> list =
                notificationRepository.findByUserIdAndReadFalse(userId);

        List<NotificationResponseDto> res =
                new ArrayList<>();

        for (Notification notification : list) {
            res.add(NotificationMapper.toResponseDto(notification));
        }

        return res;
    }

    public List<NotificationResponseDto> getNotificationsByUserIdAndType(
            Long userId,
            NotificationType type) {

        List<Notification> list =
                notificationRepository.findByUserIdAndType(userId, type);

        List<NotificationResponseDto> res =
                new ArrayList<>();

        for (Notification notification : list) {
            res.add(NotificationMapper.toResponseDto(notification));
        }

        return res;
    }
}