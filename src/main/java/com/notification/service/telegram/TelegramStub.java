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

    public void sendThresholdReviewerMessage(Task task) {
        notificationPrinter.notifyToReviewerAboutThreshold(task);
    }

    public void confirmThresholdTaskNotifyToDeveloper(Task task) {
        notificationPrinter.confirmThresholdTaskNotifyToDeveloper(task);
    }

    public class NotificationPrinter {
        private void logTasks(Task task, UserRole role) {
            NotificationData data = new NotificationData(
                    role.toString(),
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
            System.out.println("Threshold отслежен. Подтвердите отправку чтобы отправить разработчику");
            logTasks(task, UserRole.REVIEWER);
        }

        private void confirmThresholdTaskNotifyToDeveloper(Task task) {
            System.out.println("Ревьюер подтведил threshold, уведомляем разработчика...");
            logTasks(task, UserRole.REVIEWER);
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
