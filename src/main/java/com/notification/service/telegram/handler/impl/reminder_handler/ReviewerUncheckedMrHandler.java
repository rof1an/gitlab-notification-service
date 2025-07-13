package com.notification.service.telegram.handler.impl.reminder_handler;

import com.notification.service.entity.Notification;
import com.notification.service.model.NotificationType;
import com.notification.service.telegram.TelegramNotificationBot;
import com.notification.service.telegram.handler.ReminderHandler;
import com.notification.service.util.TelegramMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewerUncheckedMrHandler implements ReminderHandler {

    private final TelegramNotificationBot notificationBot;
    private final TelegramMessageFormatter messageFormatter;

    @Override
    public void handle(Notification notification) {
        notificationBot.executeMessage(
                String.valueOf(notification.getTask().getReviewer().getTelegramChatId()),
                messageFormatter.formatActiveMergeRequestRemind(notification.getTask())
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.REMIND_REVIEWER_UNCHECKED_MR;
    }
}
