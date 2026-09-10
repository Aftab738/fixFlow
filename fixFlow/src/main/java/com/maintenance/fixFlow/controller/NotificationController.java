package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.NotificationRequestDto;
import com.maintenance.fixFlow.dto.NotificationResponseDto;
import com.maintenance.fixFlow.entity.NotificationType;
import com.maintenance.fixFlow.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public NotificationResponseDto create(
            @Valid @RequestBody NotificationRequestDto dto) {
        return notificationService.createNotification(dto);
    }

    @GetMapping("/{id}")
    public NotificationResponseDto getById(@PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }

    @GetMapping("/getAll")
    public List<NotificationResponseDto> getAll() {
        return notificationService.getAllNotifications();
    }

    @PutMapping("/{id}")
    public NotificationResponseDto update(
            @Valid @RequestBody NotificationRequestDto dto,
            @PathVariable Long id) {
        return notificationService.updateNotification(dto, id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }

    @GetMapping("/userId/{userId}")
    public List<NotificationResponseDto> getByUserId(
            @PathVariable Long userId) {
        return notificationService.getNotificationsByUserId(userId);
    }

    @GetMapping("/userId/{userId}/unread")
    public List<NotificationResponseDto> getUnreadByUserId(
            @PathVariable Long userId) {
        return notificationService.getUnreadNotificationsByUserId(userId);
    }

    @GetMapping("/userId/{userId}/type/{type}")
    public List<NotificationResponseDto> getByUserIdAndType(
            @PathVariable Long userId,
            @PathVariable NotificationType type) {
        return notificationService.getNotificationsByUserIdAndType(userId, type);
    }
}