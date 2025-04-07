package com.notification.service.handler.impl.scheduler_handler;

import com.notification.service.entity.Notification;
import com.notification.service.handler.NotificationHandler;
import com.notification.service.model.NotificationType;
import com.notification.service.telegram.HiveNotificationBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeveloperThresholdFixHandler implements NotificationHandler {

    private final HiveNotificationBot notificationBot;

    @Override
    public void handle(Notification notification) {
        notificationBot.handleSendDeveloperNewFixOnThresholdAccept(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                notification.getMessage(),
                notification.getTask().getId()
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE;
    }
}