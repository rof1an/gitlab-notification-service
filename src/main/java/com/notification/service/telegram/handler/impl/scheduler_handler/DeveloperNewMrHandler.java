package com.notification.service.telegram.handler.impl.scheduler_handler;

import com.notification.service.entity.Notification;
import com.notification.service.model.NotificationType;
import com.notification.service.telegram.HiveNotificationBot;
import com.notification.service.telegram.handler.NotificationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeveloperNewMrHandler implements NotificationHandler {

    private final HiveNotificationBot notificationBot;

    @Override
    public void handle(Notification notification) {
        notificationBot.handleInteractiveCallback(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                notification.getMessage(),
                notification.getTask().getId(),
                getNotificationType(),
                "Уведомить ревьюера"
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE;
    }
}