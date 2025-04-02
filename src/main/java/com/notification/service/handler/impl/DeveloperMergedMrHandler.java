package com.notification.service.handler.impl;

import com.notification.service.entity.Notification;
import com.notification.service.handler.NotificationHandler;
import com.notification.service.model.NotificationType;
import com.notification.service.telegram.HiveNotificationBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeveloperMergedMrHandler implements NotificationHandler {

    private final HiveNotificationBot notificationBot;

    @Override
    public void handle(Notification notification) {
        notificationBot.sendMessage(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                notification.getMessage()
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_DEVELOPER_MERGED_MR_MESSAGE;
    }
}