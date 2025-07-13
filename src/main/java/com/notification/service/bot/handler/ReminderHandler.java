package com.notification.service.bot.handler;

import com.notification.service.core.entity.Notification;
import com.notification.service.core.model.NotificationType;

public interface ReminderHandler {

    void handle(Notification notification);

    NotificationType getNotificationType();
}
