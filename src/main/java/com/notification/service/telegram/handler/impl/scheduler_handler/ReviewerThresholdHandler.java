package com.notification.service.telegram.handler.impl.scheduler_handler;

import com.notification.service.entity.Notification;
import com.notification.service.model.NotificationType;
import com.notification.service.telegram.HiveNotificationBot;
import com.notification.service.telegram.handler.NotificationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewerThresholdHandler implements NotificationHandler {

    private final HiveNotificationBot notificationBot;

    @Override
    public void handle(Notification notification) {
        notificationBot.handleInteractiveCallback(
                String.valueOf(notification.getTask().getReviewer().getTelegramChatId()),
                notification.getMessage(),
                notification.getTask().getId(),
                getNotificationType(),
                "Уведомить девелопера"
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE;
    }
}
