package com.notification.service.telegram.handler.impl.scheduler_handler;

import com.notification.service.entity.Notification;
import com.notification.service.model.NotificationType;
import com.notification.service.telegram.TelegramNotificationBot;
import com.notification.service.telegram.handler.NotificationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewerNewMrHandler implements NotificationHandler {

    private final TelegramNotificationBot notificationBot;

    @Override
    public void handle(Notification notification) {
        notificationBot.executeMessage(
                String.valueOf(notification.getTask().getReviewer().getTelegramChatId()),
                notification.getMessage()
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_REVIEWER_NEW_MR_MESSAGE;
    }
}