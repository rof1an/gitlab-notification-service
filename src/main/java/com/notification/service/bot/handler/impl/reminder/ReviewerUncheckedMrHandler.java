package com.notification.service.bot.handler.impl.reminder;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.bot.handler.ReminderHandler;
import com.notification.service.bot.util.TelegramMessageFormatter;
import com.notification.service.core.entity.Notification;
import com.notification.service.core.model.NotificationType;
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
