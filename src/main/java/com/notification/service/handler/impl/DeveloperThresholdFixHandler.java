package com.notification.service.handler.impl;

import com.notification.service.entity.Notification;
import com.notification.service.handler.NotificationHandler;
import com.notification.service.service.NotificationService;
import com.notification.service.telegram.HiveNotificationBot;
import org.springframework.stereotype.Component;

@Component
public class DeveloperThresholdFixHandler implements NotificationHandler {

    @Override
    public void handle(Notification notification, HiveNotificationBot bot, NotificationService notificationService) {
        bot.handleSendDeveloperNewFixOnThresholdAccept(
                String.valueOf(notification.getTask().getDeveloper().getTelegramChatId()),
                notification.getMessage(),
                notification.getTask().getId()
        );
        notificationService.deleteNotificationById(notification.getId());
    }
}