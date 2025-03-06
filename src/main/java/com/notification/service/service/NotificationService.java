package com.notification.service.service;

import com.notification.service.entity.Task;
import com.notification.service.telegram.TelegramStub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TelegramStub telegramStub;

    public void notifyDeveloper(Task task) {
        telegramStub.sendDeveloperMessage(task);
    }

    public void notifyReviewer(Task task) {
        telegramStub.sendReviewerMessage(task);
    }

    public void notifyThresholdReviewer(Task task) {
        telegramStub.sendThresholdReviewerMessage(task);
    }

    public void notifyThresholdDeveloper(Task task) {
        telegramStub.sendThresholdDeveloperMessage(task);
    }

}
