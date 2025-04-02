package com.notification.service.handler.impl;

import com.notification.service.entity.Notification;
import com.notification.service.handler.NotificationHandler;
import com.notification.service.model.NotificationType;
import com.notification.service.telegram.HiveNotificationBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewerNewMrHandler implements NotificationHandler {

    private final HiveNotificationBot notificationBot;

    @Override
    public void handle(Notification notification) {
        notificationBot.sendMessage(
                String.valueOf(notification.getTask().getReviewer().getTelegramChatId()),
                notification.getMessage()
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_REVIEWER_NEW_MR_MESSAGE;
    }
}