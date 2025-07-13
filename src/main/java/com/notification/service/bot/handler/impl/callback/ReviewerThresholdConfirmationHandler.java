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
public class ReviewerThresholdConfirmationHandler implements CallbackNotificationHandler {

    private final TaskService taskService;

    private final TelegramMessageFormatter telegramMessageFormatter;

    @Override
    public void handle(TelegramNotificationBot notificationBot, CallbackQuery callbackQuery, Long taskId) {
        Task task = taskService.getTaskById(taskId);
        String developerChatId = String.valueOf(task.getDeveloper().getTelegramChatId());
        String messageText = telegramMessageFormatter.formatDeveloperNewThresholdMessage(task);

        notificationBot.executeMessage(developerChatId, messageText);
        taskService.updateTaskStatus(taskId, TaskStatus.NEED_FIXES);

        notificationBot.executeMessage(
                callbackQuery.getMessage().getChatId().toString(),
                "Девелопер уведомлен! ✅"
        );
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE;
    }
}
