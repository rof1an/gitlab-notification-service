package com.notification.service.telegram;

import com.notification.service.entity.Task;
import com.notification.service.model.UserRole;
import com.notification.service.util.TaskLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramStub {

    private final TaskLogger taskLogger;

    public void sendDeveloperMessage(Task task) {
        System.out.println(taskLogger.logTasks(task, UserRole.DEVELOPER));
    }

    public void sendReviewerMessage(Task task) {
        System.out.println(taskLogger.logTasks(task, UserRole.REVIEWER));
    }
}
