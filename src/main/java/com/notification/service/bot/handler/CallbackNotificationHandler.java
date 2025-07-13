package com.notification.service.bot.handler;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.core.model.NotificationType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackNotificationHandler {

    void handle(TelegramNotificationBot notificationBot, CallbackQuery callbackQuery, Long taskId);

    NotificationType getNotificationType();
}
