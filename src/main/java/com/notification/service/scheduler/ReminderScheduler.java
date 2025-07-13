package com.notification.service.scheduler;

import com.notification.service.entity.Task;
import com.notification.service.model.NotificationType;
import com.notification.service.model.TaskStatus;
import com.notification.service.service.NotificationService;
import com.notification.service.service.TaskService;
import com.notification.service.telegram.handler.ReminderHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderScheduler {

    private final TaskService taskService;
    private final NotificationService notificationService;
    private final List<ReminderHandler> handlers;

    @Scheduled(fixedRate = 100000)
    private void createNotification() {
        List<Task> tasks = taskService.getTasksByStatuses(List.of(TaskStatus.REVIEW, TaskStatus.NEED_FIXES));
        tasks.stream()
                .filter(task -> !notificationService.existsForTask(task))
                .forEach(task -> notificationService
                        .createNotification(task, NotificationType.REMIND_REVIEWER_UNCHECKED_MR));
    }

    @Scheduled(fixedRate = 5000)
    private void scheduleNotifications() {
        notificationService.getAllNotifications().forEach(notification -> {
            handlers.stream()
                    .filter(handler -> handler.getNotificationType()
                            .equals(notification.getNotificationType()))
                    .findFirst()
                    .ifPresent(handler -> {
                        handler.handle(notification);
                        notificationService.deleteNotificationById(notification.getId());
                    });
        });
    }
}
