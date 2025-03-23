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

    private final TelegramStub telegramStub;

    public void notifyDeveloper(Task task) {
        String message = String.format("Новая задача для разработчика: %s\nСсылка: %s",
                task.getTitle(), task.getLinkToMr());

        saveNotification(message, createNotificationByTask(task, message, NotificationType.SEND_DEVELOPER_NEW_MR));
    }

    public void notifyReviewer(Task task) {
        String message = String.format("Новая задача на ревью: %s\nСсылка: %s",
                task.getTitle(), task.getLinkToMr());

        saveNotification(message, createNotificationByTask(task, message, NotificationType.SEND_DEVELOPER_NEW_MR));
    }

    public void saveNotification(String message, Notification notification) {
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public Notification createNotificationByTask(Task task, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setNotificationType(notificationType);
        notification.setMessage(message);
        notification.setTask(task);
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void notifyThresholdReviewer(Task task) {
        telegramStub.sendThresholdReviewerMessage(task);
    }

    public void confirmThresholdTaskNotifyToDeveloper(Task task) {
        telegramStub.confirmThresholdTaskNotifyToDeveloper(task);
    }

    public void deleteNotificationById(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
