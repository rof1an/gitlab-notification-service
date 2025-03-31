package com.notification.service.scheduler;

import com.notification.service.entity.Notification;
import com.notification.service.service.NotificationService;
import com.notification.service.telegram.HiveNotificationBot;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final HiveNotificationBot hiveNotificationBot;

    private final NotificationService notificationService;

    @Scheduled(fixedRate = 5000)
    public void scheduleNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();

        notifications.forEach(notification -> {
            switch (notification.getNotificationType()) {
                case SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE -> {
                    handleSendDeveloperNewMrNotification(notification);
                }
                case SEND_REVIEWER_NEW_MR_MESSAGE -> {
                    handleSendReviewerNewMrNotification(notification);
                }
                case SEND_DEVELOPER_MERGED_MR_MESSAGE -> {
                    handleSendDeveloperMergedTakNotification(notification);
                }
                case SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE -> {
                    handleSendReviewerThresholdAccept(notification);
                }
                case SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE -> {
                    handleSendDeveloperNewFixOnThresholdAccept(notification);
                }
            }
        });
    }

    private void handleSendReviewerThresholdAccept(Notification notification) {
        hiveNotificationBot.sendReviewerThresholdAccept(
                String.valueOf(notification.getTask().getReviewer().getTelegramChatId()),
                notification.getMessage(),
                notification.getTask().getId()
        );
        notificationService.deleteNotificationById(notification.getId());
    }

    private void handleSendDeveloperMergedTakNotification(Notification notification) {
        hiveNotificationBot.sendMessage(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                notification.getMessage()
        );
        notificationService.deleteNotificationById(notification.getId());
    }

    private void handleSendDeveloperNewMrNotification(Notification notification) {
        hiveNotificationBot.sendDeveloperNewTaskMessageWithConfirmation(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                notification.getMessage(),
                notification.getTask().getId()
        );
        notificationService.deleteNotificationById(notification.getId());
    }

    private void handleSendReviewerNewMrNotification(Notification notification) {
        hiveNotificationBot.sendMessage(
                String.valueOf(notification.getTask().getReviewer().getTelegramChatId()),
                notification.getMessage()
        );
        notificationService.deleteNotificationById(notification.getId());
    }

    private void handleSendDeveloperNewFixOnThresholdAccept(Notification notification) {
        hiveNotificationBot.handleSendDeveloperNewFixOnThresholdAccept(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                notification.getMessage(),
                notification.getTask().getId()
        );
        notificationService.deleteNotificationById(notification.getId());
    }
}
