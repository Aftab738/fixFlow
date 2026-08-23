package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Notification;
import com.maintenance.fixFlow.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndReadFalse(Long userId);

    List<Notification> findByUserIdAndType(
            Long userId,
            NotificationType type
    );
}
