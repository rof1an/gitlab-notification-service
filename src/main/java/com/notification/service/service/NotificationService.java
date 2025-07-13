package com.notification.service.service;

import com.notification.service.entity.Notification;
import com.notification.service.entity.Task;
import com.notification.service.model.NotificationType;
import com.notification.service.repository.NotificationRepository;
import com.notification.service.telegram.TelegramStub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationFactory notificationFactory;

    private final TelegramStub telegramStub;

    public void createNotification(Task task, NotificationType type) {
        Notification notification = notificationFactory.createNotification(task, type);
        notificationRepository.save(notification);
    }

    public void confirmThresholdTaskNotifyToDeveloper(Task task) {
        telegramStub.confirmThresholdTaskNotifyToDeveloper(task);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void deleteNotificationById(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public boolean existsForTask(Task task) {
        return notificationRepository.existsByTaskId(task.getId());
    }
}