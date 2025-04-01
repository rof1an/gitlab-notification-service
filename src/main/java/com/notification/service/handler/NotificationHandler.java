package com.notification.service.handler;

import com.notification.service.entity.Notification;
import com.notification.service.model.NotificationType;

public interface NotificationHandler {

    void handle(Notification notification);

    NotificationType getNotificationType();
}