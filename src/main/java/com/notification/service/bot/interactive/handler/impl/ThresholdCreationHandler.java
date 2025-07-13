package com.notification.service.bot.interactive.handler.impl;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.bot.interactive.handler.InteractiveHandler;
import com.notification.service.bot.interactive.model.ThresholdModel;
import com.notification.service.bot.interactive.model.session.ThresholdCreationSession;
import com.notification.service.bot.interactive.service.WebhookService;
import com.notification.service.bot.interactive.service.sessionService.ThresholdCreationSessionService;
import com.notification.service.bot.util.TelegramKeyboardFactory;
import com.notification.service.core.dto.TaskDto;
import com.notification.service.core.entity.Task;
import com.notification.service.core.model.SessionType;
import com.notification.service.core.model.TaskStatus;
import com.notification.service.core.repository.TaskRepository;
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
public class ThresholdCreationHandler implements InteractiveHandler {

    private final ThresholdCreationSessionService thresholdCreationSessionService;
    private final TaskRepository taskRepository;
    private final WebhookService webhookService;

    @Override
    public SessionType getSessionType() {
        return SessionType.THRESHOLD_CREATING;
    }

    @Override
    public boolean isSessionInProgress(String chatId) {
        ThresholdCreationSession session = thresholdCreationSessionService.getSession(chatId);
        return session != null && session.getStep() != ThresholdCreationSession.Step.COMPLETE;
    }

    @Override
    public void processCallback(TelegramNotificationBot bot, String chatId, String input) {
        ThresholdCreationSession session = thresholdCreationSessionService.getOrCreateSession(chatId);
        ThresholdModel thresholdModel = session.getThresholdModel();

        switch (session.getStep()) {
            case SELECT_MERGE_REQUEST -> {
                Long selectedTaskId = Long.parseLong(input);
                Task selectedTask = taskRepository.findById(selectedTaskId)
                        .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + selectedTaskId));

                thresholdModel.setMrTitle(selectedTask.getTitle());
                thresholdModel.setLinkToMr(selectedTask.getLinkToMr());
                thresholdModel.setDeveloperId(selectedTask.getDeveloper().getId());
                thresholdModel.setReviewerId(selectedTask.getReviewer().getId());

                createThreshold(bot, chatId, thresholdModel);
                session.setStep(ThresholdCreationSession.Step.COMPLETE);
            }
            default -> bot.executeMessage(chatId, "Что-то пошло не так. Попробуйте заново.");
        }
    }

    @Override
    public void cancelSession(String chatId) {
        thresholdCreationSessionService.clearSession(chatId);
    }

    @Override
    public void startSession(TelegramNotificationBot bot, String chatId) {
        ThresholdCreationSession startedSession = thresholdCreationSessionService.getOrCreateSession(chatId);
        startedSession.setStep(ThresholdCreationSession.Step.SELECT_MERGE_REQUEST);

        List<Task> tasks = taskRepository.findAllByStatus(TaskStatus.REVIEW)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with status - REVIEW"));

        createMergeRequestsChooseButtons(bot, chatId, tasks, "Выберите МР для создания Threshold:");
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

    private void createThreshold(TelegramNotificationBot bot, String chatId, ThresholdModel model) {
        try {
            TaskDto createdThreshold = webhookService.createThreshold(model);
            bot.executeMessage(chatId, "Вы успешно создали Threshold для МР: " + createdThreshold.getTitle());
        } catch (Exception e) {
            log.info("Error creating threshold", e);
            bot.executeMessage(chatId, "Ошибка при создании Threshold. Попробуйте снова.");
        }
    }
}
