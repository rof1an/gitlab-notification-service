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

    private void notify(Task task, NotificationType type) {
        Notification notification = notificationFactory.createNotification(task, type);
        notificationRepository.save(notification);
    }

    public void notifyDeveloperMrReviewRequestMessage(Task task) {
        notify(task, NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE);
    }

    public void notifyReviewerNewMrMessage(Task task) {
        notify(task, NotificationType.SEND_REVIEWER_NEW_MR_MESSAGE);
    }

    public void notifyDeveloperMergedMrMessage(Task task) {
        notify(task, NotificationType.SEND_DEVELOPER_MERGED_MR_MESSAGE);
    }

    public void notifyReviewerThresholdOnMrRequestMessage(Task task) {
        notify(task, NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE);
    }

    public void notifyDeveloperNewFixOnThresholdRequestMessage(Task task) {
        notify(task, NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE);
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
}
