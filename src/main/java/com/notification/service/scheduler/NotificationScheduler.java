package com.notification.service.scheduler;

import com.notification.service.entity.Notification;
import com.notification.service.model.TaskStatus;
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
        List<Notification> notifications = notificationService.getAllNotifications().stream()
                .filter(notification -> notification.getTask().getStatus().equals(TaskStatus.OPEN))
                .toList();

        notifications.forEach(notification -> {
            switch (notification.getNotificationType()) {
                case SEND_DEVELOPER_NEW_MR -> {
                    hiveNotificationBot.sendDeveloperNewTaskMessageWithConfirmation(
                            String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                            "Новый MR создан! Нажмите кнопку, чтобы уведомить ревьюера.",
                            notification.getTask().getId()
                    );
                    notificationService.deleteNotificationById(notification.getId());
                }
                case SEND_REVIEWER_NEW_MR -> {
                    hiveNotificationBot.sendMessage(
                            String.valueOf(notification.getTask().getReviewer().getTelegramChatId()),
                            "Получен новый МР на проверку!"
                    );
                    notificationService.deleteNotificationById(notification.getId());
                }
            }
        });
    }
}
