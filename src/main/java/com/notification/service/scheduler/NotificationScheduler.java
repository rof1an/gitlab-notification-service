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
                .filter(notification -> {
                    return notification.getTask().getStatus().equals(TaskStatus.OPEN);
                })
                .toList();

        notifications.forEach(notification -> {
            hiveNotificationBot.sendMessageWithConfirmation(
                    String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                    "Новый MR создан! Нажмите кнопку, чтобы уведомить ревьюера.",
                    notification.getTask().getId()
            );
            notificationService.deleteNotificationById(notification.getId());
        });
    }
}
