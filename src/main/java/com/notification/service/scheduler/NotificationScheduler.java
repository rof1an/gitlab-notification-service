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
                case SEND_DEVELOPER_NEW_MR -> {
                    handleSendDeveloperNewMrNotification(notification);
                }
                case SEND_REVIEWER_NEW_MR -> {
                    handleSendReviewerNewMrNotification(notification);
                }
                case SEND_DEVELOPER_MERGED_MR -> {
                    handleSendDeveloperMergedTakNotification(notification);
                }
            }
        });
    }

    private void handleSendDeveloperMergedTakNotification(Notification notification) {
        hiveNotificationBot.sendDeveloperMergedTakNotification(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                notification.getMessage()
        );
        notificationService.deleteNotificationById(notification.getId());
    }

    private void handleSendDeveloperNewMrNotification(Notification notification) {
        hiveNotificationBot.sendDeveloperNewTaskMessageWithConfirmation(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                "Новый MR создан! Нажмите кнопку, чтобы уведомить ревьюера.",
                notification.getTask().getId()
        );
        notificationService.deleteNotificationById(notification.getId());
    }

    private void handleSendReviewerNewMrNotification(Notification notification) {
        hiveNotificationBot.sendMessage(
                String.valueOf(notification.getTask().getReviewer().getTelegramChatId()),
                "Получен новый МР на проверку!"
        );
        notificationService.deleteNotificationById(notification.getId());
    }
}
