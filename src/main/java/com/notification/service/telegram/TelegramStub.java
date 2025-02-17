package com.notification.service.telegram;

import com.notification.service.entity.Task;
import com.notification.service.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramStub {

    public void sendDeveloperMessage(Task newTask) {
        DataPrint.notifyToDeveloperTelegram(newTask);
    }

    private static class DataPrint {
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
}
