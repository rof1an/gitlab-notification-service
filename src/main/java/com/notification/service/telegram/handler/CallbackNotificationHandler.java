package com.notification.service.telegram.handler;

import com.notification.service.model.NotificationType;
import com.notification.service.telegram.HiveNotificationBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackNotificationHandler {

    void handle(HiveNotificationBot notificationBot, CallbackQuery callbackQuery, Long taskId);

    NotificationType getNotificationType();
}
