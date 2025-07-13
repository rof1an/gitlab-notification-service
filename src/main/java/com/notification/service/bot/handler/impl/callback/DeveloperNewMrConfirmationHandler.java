package com.notification.service.bot.handler.impl.callback;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.bot.handler.CallbackNotificationHandler;
import com.notification.service.bot.util.TelegramMessageFormatter;
import com.notification.service.core.entity.Task;
import com.notification.service.core.model.NotificationType;
import com.notification.service.core.model.TaskStatus;
import com.notification.service.core.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class DeveloperNewMrConfirmationHandler implements CallbackNotificationHandler {

    private final TaskService taskService;

    private final TelegramMessageFormatter telegramMessageFormatter;

    @Override
    public void handle(TelegramNotificationBot notificationBot, CallbackQuery callbackQuery, Long taskId) {
        Task task = taskService.getTaskById(taskId);
        String reviewerChatId = String.valueOf(task.getReviewer().getTelegramChatId());
        String messageText = telegramMessageFormatter.formatReviewerNotificationMessage(task);

        notificationBot.executeMessage(reviewerChatId, messageText);
        taskService.updateTaskStatus(taskId, TaskStatus.REVIEW);

        notificationBot.executeMessage(
                callbackQuery.getMessage().getChatId().toString(),
                "Ревьюер уведомлен! ✅"
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE;
    }
}
