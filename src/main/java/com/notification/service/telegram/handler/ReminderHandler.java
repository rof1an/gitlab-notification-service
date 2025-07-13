package com.notification.service.telegram.handler;

import com.notification.service.entity.Notification;
import com.notification.service.model.NotificationType;

public interface ReminderHandler {

    void handle(Notification notification);

    NotificationType getNotificationType();
}
