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

    public void notifyDeveloperMrReviewRequestMessage(Task task) {
        String message = String.format("Новый МР/Новые изменения в МР: %s\nСсылка: %s",
                task.getTitle(), task.getLinkToMr());

        saveNotification(createNotificationByTask(task, message, NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE));
    }

    public void notifyReviewerNewMrMessage(Task task) {
        String message = String.format("Новая задача на ревью: %s\nСсылка: %s",
                task.getTitle(), task.getLinkToMr());

        saveNotification(createNotificationByTask(task, message, NotificationType.SEND_REVIEWER_NEW_MR_MESSAGE));
    }

    public void notifyDeveloperMergedMrMessage(Task mergedTask) {
        String message = String.format("Ваш MR был успешно смержен!: %s\nСсылка: %s\nReviewer: %s",
                mergedTask.getTitle(), mergedTask.getLinkToMr(), mergedTask.getReviewer().getUsername());

        saveNotification(createNotificationByTask(mergedTask, message, NotificationType.SEND_DEVELOPER_MERGED_MR_MESSAGE));
    }

    public void saveNotification(Notification notification) {
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

    public void notifyReviewerThresholdOnMrRequestMessage(Task task) {
        String message = "Отслежен новый threshold, подтвердите отправку девелоперу";

        saveNotification(createNotificationByTask(task, message, NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE));
    }

    public void confirmThresholdTaskNotifyToDeveloper(Task task) {
        telegramStub.confirmThresholdTaskNotifyToDeveloper(task);
    }

    public void notifyDeveloperNewFixOnThresholdRequestMessage(Task task) {
        String message = "Отслежено новое изменение на threshold, подтвердите отправку ревьюеру";

        saveNotification(createNotificationByTask(task, message, NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE));
    }

    public void deleteNotificationById(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
