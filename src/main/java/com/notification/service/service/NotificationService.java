package com.notification.service.service;

import com.notification.service.entity.Task;
import com.notification.service.telegram.HiveNotificationBot;
import com.notification.service.telegram.TelegramStub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final HiveNotificationBot hiveNotificationBot;

    private final TelegramStub telegramStub;

    public void notifyDeveloper(Task task) {
        String message = String.format("Новая задача для разработчика: %s\nСсылка: %s",
                task.getTitle(), task.getLinkToMr());

        hiveNotificationBot.sendMessage(
                String.valueOf(task.getDeveloper().getId()),
                message
        );
    }

    public void notifyReviewer(Task task) {
        String message = String.format("Новая задача на ревью: %s\nСсылка: %s",
                task.getTitle(), task.getLinkToMr());

        hiveNotificationBot.sendMessage(
                String.valueOf(task.getReviewer().getId()), message
        );
    }

    public void notifyThresholdReviewer(Task task) {
        telegramStub.sendThresholdReviewerMessage(task);
    }

    public void acceptThresholdTaskNotify(Task task){
        telegramStub.acceptThresholdTaskNotify(task);
    }
}
