package com.notification.service.util;

import com.notification.service.entity.Task;
import com.notification.service.model.UserRole;
import org.springframework.stereotype.Component;

@Component
public class MergeRequestDataPrints {

    private static String logTasks(Task task, UserRole role) {
        return String.format("""
                        Ваша роль - %s
                        Данные о новом MergeRequest:
                        Название: %s
                        Ссылка на MR: %s
                        Статус: %s
                        """,
                role,
                task.getTitle(),
                task.getLinkToMr(),
                task.getStatus()
        );
    }

    public static void notifyToDeveloperTelegram(Task task) {
        System.out.println(logTasks(task, UserRole.DEVELOPER));
    }

    public static void notifyToReviewerTelegram(Task task) {
        System.out.println(logTasks(task, UserRole.REVIEWER));
    }
}
