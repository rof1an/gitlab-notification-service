package com.notification.service.bot.handler.impl.scheduler;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.bot.handler.NotificationHandler;
import com.notification.service.core.entity.Notification;
import com.notification.service.core.model.NotificationType;
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