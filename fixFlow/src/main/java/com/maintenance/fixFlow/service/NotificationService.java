package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.entity.Notification;
import com.maintenance.fixFlow.entity.NotificationType;
import com.maintenance.fixFlow.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification updateNotification(
            Notification notification, Long id) {

        Optional<Notification> existingNotification =
                notificationRepository.findById(id);

        if (existingNotification.isPresent()) {

            Notification n = existingNotification.get();

            n.setMessage(notification.getMessage());
            n.setType(notification.getType());
            n.setRead(notification.isRead());
            n.setUser(notification.getUser());

            return notificationRepository.save(n);
        }

        return null;
    }

    public String deleteNotification(Long id) {

        Optional<Notification> existingNotification =
                notificationRepository.findById(id);

        if (existingNotification.isPresent()) {
            notificationRepository.deleteById(id);
            return "Notification Deleted";
        }

        return "Notification not found";
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }

    public List<Notification> getNotificationsByUserIdAndType(
            Long userId,
            NotificationType type) {

        return notificationRepository.findByUserIdAndType(userId, type);
    }
}