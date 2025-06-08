package com.notification.service.telegram.handler.impl.callback_handler;

import com.notification.service.entity.Task;
import com.notification.service.model.NotificationType;
import com.notification.service.model.TaskStatus;
import com.notification.service.service.TaskService;
import com.notification.service.telegram.TelegramNotificationBot;
import com.notification.service.telegram.handler.CallbackNotificationHandler;
import com.notification.service.util.TelegramMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class DeveloperThresholdFixConfirmationHandler implements CallbackNotificationHandler {

    private final TaskService taskService;

    private final TelegramMessageFormatter telegramMessageFormatter;

    @Override
    public void handle(TelegramNotificationBot notificationBot, CallbackQuery callbackQuery, Long taskId) {
        Task task = taskService.getTaskById(taskId);
        String reviewerChatId = String.valueOf(task.getReviewer().getTelegramChatId());
        String messageText = telegramMessageFormatter.formatReviewerNewFixOnThreshold(task);

        notificationBot.executeMessage(reviewerChatId, messageText);
        taskService.updateTaskStatus(taskId, TaskStatus.REVIEW);

        notificationBot.executeMessage(
                callbackQuery.getMessage().getChatId().toString(),
                "Ревьюер уведомлен! ✅"
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE;
    }
}
