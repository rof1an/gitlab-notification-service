package com.notification.service.handler;

import com.notification.service.entity.Notification;
import com.notification.service.service.NotificationService;
import com.notification.service.telegram.HiveNotificationBot;

public interface NotificationHandler {

    void handle(Notification notification, HiveNotificationBot bot, NotificationService notificationService);
}