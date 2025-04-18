package com.notification.service.scheduler;

import com.notification.service.service.NotificationService;
import com.notification.service.telegram.handler.NotificationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    private final List<NotificationHandler> handlers;

    @Scheduled(fixedRate = 5000)
    public void scheduleNotifications() {
        notificationService.getAllNotifications().forEach(notification -> {
            handlers.stream()
                    .filter(handler -> handler.getNotificationType().equals(notification.getNotificationType()))
                    .findFirst()
                    .ifPresent(handler -> {
                        handler.handle(notification);
                        notificationService.deleteNotificationById(notification.getId());
                    });
        });
    }
}