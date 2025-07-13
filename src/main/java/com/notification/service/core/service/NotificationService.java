package com.notification.service.core.service;

import com.notification.service.bot.TelegramStub;
import com.notification.service.core.entity.Notification;
import com.notification.service.core.entity.Task;
import com.notification.service.core.model.NotificationType;
import com.notification.service.core.repository.NotificationRepository;
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