package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.NotificationRequestDto;
import com.maintenance.fixFlow.dto.NotificationResponseDto;
import com.maintenance.fixFlow.entity.Notification;
import com.maintenance.fixFlow.entity.NotificationType;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.mapper.NotificationMapper;
import com.maintenance.fixFlow.repository.NotificationRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .orElse(null);

        Notification notification =
                NotificationMapper.toEntity(dto, user);

        Notification savedNotification =
                notificationRepository.save(notification);

        return NotificationMapper.toResponseDto(savedNotification);
    }

    public NotificationResponseDto getNotificationById(Long id) {

        Optional<Notification> notification =
                notificationRepository.findById(id);

        if (notification.isPresent()) {
            return NotificationMapper.toResponseDto(
                    notification.get());
        }

        return null;
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

        Optional<Notification> existingNotification =
                notificationRepository.findById(id);

        if (existingNotification.isPresent()) {

            Notification notification =
                    existingNotification.get();

            User user = userRepository.findById(dto.getUserId())
                    .orElse(null);

            notification.setMessage(dto.getMessage());
            notification.setType(dto.getType());
            notification.setRead(dto.isRead());
            notification.setUser(user);

            Notification savedNotification =
                    notificationRepository.save(notification);

            return NotificationMapper.toResponseDto(
                    savedNotification);
        }

        return null;
    }

    public String deleteNotification(Long id) {

        Optional<Notification> notification =
                notificationRepository.findById(id);

        if (notification.isPresent()) {
            notificationRepository.delete(notification.get());
            return "Notification Deleted";
        }

        return "Notification not found";
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