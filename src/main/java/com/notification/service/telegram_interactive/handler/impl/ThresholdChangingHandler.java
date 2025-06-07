package com.notification.service.telegram_interactive.handler.impl;

import com.notification.service.entity.Task;
import com.notification.service.model.SessionType;
import com.notification.service.model.TaskStatus;
import com.notification.service.repository.TaskRepository;
import com.notification.service.telegram.TelegramNotificationBot;
import com.notification.service.telegram_interactive.WebhookService;
import com.notification.service.telegram_interactive.handler.InteractiveHandler;
import com.notification.service.telegram_interactive.model.ThresholdModel;
import com.notification.service.telegram_interactive.model.session.ThresholdChangingSession;
import com.notification.service.telegram_interactive.service.ThresholdChangingService;
import com.notification.service.util.TelegramKeyboardFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThresholdChangingHandler implements InteractiveHandler {

    private final ThresholdChangingService thresholdChangingService;
    private final WebhookService webhookService;
    private final TaskRepository taskRepository;

    @Override
    public void processCallback(TelegramNotificationBot bot, String chatId, String input) {
        ThresholdChangingSession session = thresholdChangingService.getOrCreateSession(chatId);
        ThresholdModel thresholdChangingModel = session.getThresholdModel();

        switch (session.getStep()) {
            case SELECT_THRESHOLD -> {
                Long selectedTaskId = Long.parseLong(input);
                Task selectedTask = taskRepository.findById(selectedTaskId)
                        .orElseThrow(() -> new EntityNotFoundException("Task not found with id = " + selectedTaskId));

                thresholdChangingModel.setMrTitle(selectedTask.getTitle());
                thresholdChangingModel.setLinkToMr(selectedTask.getLinkToMr());
                thresholdChangingModel.setDeveloperId(selectedTask.getDeveloper().getId());
                thresholdChangingModel.setReviewerId(selectedTask.getReviewer().getId());

                createThresholdChange(bot, chatId, thresholdChangingModel);
                session.setStep(ThresholdChangingSession.Step.COMPLETE);
            }
            default -> bot.executeMessage(chatId, "Что-то пошло не так. Попробуйте заново.");
        }
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.THRESHOLD_CHANGING;
    }

    @Override
    public boolean isSessionInProgress(String chatId) {
        ThresholdChangingSession session = thresholdChangingService.getSession(chatId);
        return session != null && session.getStep() != ThresholdChangingSession.Step.COMPLETE;
    }

    @Override
    public void cancelSession(String chatId) {
        thresholdChangingService.clearSession(chatId);
    }

    @Override
    public void startSession(TelegramNotificationBot bot, String chatId) {
        ThresholdChangingSession session = thresholdChangingService.getOrCreateSession(chatId);
        session.setStep(ThresholdChangingSession.Step.SELECT_THRESHOLD);

        TaskStatus needFixesStatus = TaskStatus.NEED_FIXES;
        List<Task> tasks = taskRepository.findAllByStatus(needFixesStatus)
                .orElseThrow(() -> new EntityNotFoundException("Tasks not found with status = " + needFixesStatus));

        createMergeRequestsChooseButtons(bot, chatId, tasks, "Выбери МР для уведомления об изменении:");
    }

    private void createMergeRequestsChooseButtons(TelegramNotificationBot bot, String chatId,
                                                  List<Task> tasks, String action) {
        InlineKeyboardMarkup inlineKeyboardMarkup = TelegramKeyboardFactory.createSingleColumnKeyboard(
                tasks,
                task -> String.format("TITLE: %s | LINK: %s", task.getTitle(), task.getLinkToMr()),
                task -> String.valueOf(task.getId())
        );

        SendMessage sendMessage = new SendMessage(chatId, action);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        bot.executeMessage(sendMessage);
    }

    private void createThresholdChange(TelegramNotificationBot bot, String chatId, ThresholdModel model) {
        try {
            webhookService.createThresholdChange(model);
        } catch (Exception e) {
            log.info("Error creating threshold", e);
            bot.executeMessage(chatId, "Ошибка при уведомлении об изменении Threshold. Попробуйте снова.");
        }
    }
}
