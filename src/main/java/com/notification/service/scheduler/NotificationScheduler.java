package com.notification.service.scheduler;

import com.notification.service.handler.NotificationHandler;
import com.notification.service.model.NotificationType;
import com.notification.service.service.NotificationService;
import com.notification.service.telegram.HiveNotificationBot;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final HiveNotificationBot hiveNotificationBot;

    private final NotificationService notificationService;

    private final Map<NotificationType, NotificationHandler> handlers;

    @Scheduled(fixedRate = 5000)
    public void scheduleNotifications() {
        notificationService.getAllNotifications().forEach(notification -> {
            NotificationHandler handler = handlers.get(notification.getNotificationType());
            if (handler != null) {
                handler.handle(notification, hiveNotificationBot, notificationService);
                notificationService.deleteNotificationById(notification.getId());
            }
        });
    }
}