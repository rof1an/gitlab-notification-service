package com.notification.service.bot.interactive.handler.impl;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.bot.interactive.handler.InteractiveHandler;
import com.notification.service.bot.interactive.model.MergeRequestModel;
import com.notification.service.bot.interactive.model.session.MergeRequestMergingSession;
import com.notification.service.bot.interactive.service.WebhookService;
import com.notification.service.bot.interactive.service.sessionService.MergeRequestMergingSessionService;
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
public class MergeRequestMergingHandler implements InteractiveHandler {

    private final TaskRepository taskRepository;
    private final WebhookService webhookService;
    private final MergeRequestMergingSessionService sessionService;

    @Override
    public SessionType getSessionType() {
        return SessionType.MERGE_MR;
    }

    @Override
    public boolean isSessionInProgress(String chatId) {
        MergeRequestMergingSession session = sessionService.getSession(chatId);
        return session != null && session.getStep() != MergeRequestMergingSession.Step.COMPLETE;
    }

    @Override
    public void processCallback(TelegramNotificationBot bot, String chatId, String input) {
        MergeRequestMergingSession session = sessionService.getOrCreateSession(chatId);
        MergeRequestModel mergeRequestModel = session.getMergeRequestModel();

        switch (session.getStep()) {
            case SELECT_MERGE_REQUEST -> {
                Long selectedTaskId = Long.parseLong(input);
                Task selectedTask = taskRepository.findById(selectedTaskId)
                        .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + selectedTaskId));

                mergeRequestModel.setTitle(selectedTask.getTitle());
                mergeRequestModel.setLinkToMr(selectedTask.getLinkToMr());
                mergeRequestModel.setDeveloperId(selectedTask.getDeveloper().getId());
                mergeRequestModel.setReviewerId(selectedTask.getReviewer().getId());

                createMergeRequestMerging(bot, chatId, mergeRequestModel);
                session.setStep(MergeRequestMergingSession.Step.COMPLETE);
            }
            default -> bot.executeMessage(chatId, "Что-то пошло не так. Попробуйте заново.");
        }
    }

    public void createMergeRequestMerging(TelegramNotificationBot bot, String chatId, MergeRequestModel model) {
        try {
            TaskDto createdTask = webhookService.createMergeRequestMerging(model);
            bot.executeMessage(chatId, "Вы успешно смержили МР: " + createdTask.getTitle());
        } catch (Exception e) {
            log.debug("Error creating merge request", e);
            bot.executeMessage(chatId, "Ошибка при мерже MР. Попробуйте снова.");
        }
    }

    @Override
    public void cancelSession(String chatId) {
        sessionService.clearSession(chatId);
    }

    @Override
    public void startSession(TelegramNotificationBot bot, String chatId) {
        MergeRequestMergingSession session = sessionService.getOrCreateSession(chatId);
        session.setStep(MergeRequestMergingSession.Step.SELECT_MERGE_REQUEST);

        List<Task> tasks = taskRepository.findAllByStatus(TaskStatus.REVIEW)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with status - REVIEW"));

        createMergeRequestsMergingChooseButtons(bot, chatId, tasks, "Выберите МР для мержа:");
    }

    private void createMergeRequestsMergingChooseButtons(TelegramNotificationBot bot, String chatId,
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
}
