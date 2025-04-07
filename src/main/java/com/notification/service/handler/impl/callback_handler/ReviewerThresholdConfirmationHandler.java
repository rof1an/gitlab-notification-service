package com.notification.service.handler.impl.callback_handler;

import com.notification.service.entity.Task;
import com.notification.service.handler.CallbackNotificationHandler;
import com.notification.service.model.NotificationType;
import com.notification.service.model.TaskStatus;
import com.notification.service.service.TaskService;
import com.notification.service.telegram.HiveNotificationBot;
import com.notification.service.util.TelegramMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class ReviewerThresholdConfirmationHandler implements CallbackNotificationHandler {

    private final TaskService taskService;

    private final TelegramMessageFormatter telegramMessageFormatter;

    @Override
    public void handle(HiveNotificationBot notificationBot, CallbackQuery callbackQuery, Long taskId) {
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
