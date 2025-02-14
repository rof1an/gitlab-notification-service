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
        telegramStub.getDeveloperMessage(task);
    }

    public void notifyReviewer(Long taskId) {
        telegramStub.getReviewerMessage(taskId);
    }

    public void notifySystem() {
        telegramStub.notifySystem();
    }
}
