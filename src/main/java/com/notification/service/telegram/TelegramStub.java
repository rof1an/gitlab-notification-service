package com.notification.service.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.service.entity.Task;
import com.notification.service.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramStub {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void sendDeveloperMessage(Task newTask) {
        DataPrint.notifyToDeveloperTelegram(newTask);
    }

    public void sendReviewerMessage(Task task) {
        DataPrint.notifyToReviewerTelegram(task);
    }

    private static class DataPrint {
        private static String logTasks(Task task, UserRole role) {
            NotificationData data = new NotificationData(
                    role.toString(),
                    task.getTitle(),
                    task.getLinkToMr(),
                    task.getStatus().name(),
                    task.getDeveloper().getId(),
                    task.getReviewer().getId()
            );

            try {
                return objectMapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка сериализации JSON", e);
            }
        }

        public static void notifyToDeveloperTelegram(Task task) {
            System.out.println(logTasks(task, UserRole.DEVELOPER));
        }

        public static void notifyToReviewerTelegram(Task task) {
            System.out.println(logTasks(task, UserRole.REVIEWER));
        }

        private record NotificationData(
                String role,
                String title,
                String linkToMr,
                String status,
                Long developerId,
                Long reviewerId
        ) {}
    }
}
