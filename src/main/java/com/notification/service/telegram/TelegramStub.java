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

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationPrinter notificationPrinter = new NotificationPrinter();

    public void sendDeveloperMessage(Task newTask) {
        notificationPrinter.logTasks(newTask);
    }

    public void sendReviewerMessage(Task task) {
        notificationPrinter.logTasks(task);
    }

    public void sendThresholdReviewerMessage(Task task) {
        notificationPrinter.notifyToReviewerAboutThreshold(task);
    }

    public void sendThresholdDeveloperMessage(Task task) {
        notificationPrinter.notifyToDeveloperAboutThreshold(task);
    }

    public class NotificationPrinter {
        private void logTasks(Task task) {
            NotificationData data = new NotificationData(
                    UserRole.DEVELOPER.toString(),
                    task.getTitle(),
                    task.getLinkToMr(),
                    task.getStatus().name(),
                    task.getDeveloper().getId(),
                    task.getReviewer().getId()
            );

            try {
                System.out.println(data);
                objectMapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка сериализации JSON", e);
            }
        }

        private void notifyToReviewerAboutThreshold(Task task) {
            System.out.println("Threshold reached! Reviewer must confirm notification.");
            // Здесь можно отправить сообщение в Telegram с кнопками "Confirm" и "Deny"
        }

        private void notifyToDeveloperAboutThreshold(Task task) {
            System.out.println("Reviewer confirmed threshold, notifying developer...");
        }

        private record NotificationData(
                String role,
                String title,
                String linkToMr,
                String status,
                Long developerId,
                Long reviewerId
        ) {
        }
    }
}
