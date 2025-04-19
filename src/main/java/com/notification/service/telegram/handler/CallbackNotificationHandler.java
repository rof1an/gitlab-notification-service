package com.notification.service.telegram.handler;

import com.notification.service.model.NotificationType;
import com.notification.service.telegram.TelegramNotificationBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackNotificationHandler {

    void handle(TelegramNotificationBot notificationBot, CallbackQuery callbackQuery, Long taskId);

    NotificationType getNotificationType();
}
