package com.notification.service.util;

import com.notification.service.entity.Task;
import com.notification.service.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MergeRequestDataPrints {

    private static String logTasks(Task task, UserRole role) {
        return String.format("""
                        Ваша роль - %s
                        Данные о новых MergeRequests:
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

    public static void notifyToDeveloperTelegram(List<Task> tasks) {
        tasks.forEach(task -> System.out.println(logTasks(task, UserRole.DEVELOPER)));
    }

    public static void notifyToReviewerTelegram(List<Task> tasks) {
        tasks.forEach(task -> System.out.println(logTasks(task, UserRole.REVIEWER)));
    }
}
