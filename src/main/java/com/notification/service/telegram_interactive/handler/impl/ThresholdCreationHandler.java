package com.notification.service.telegram_interactive.handler.impl;

import com.notification.service.entity.Task;
import com.notification.service.model.NotificationType;
import com.notification.service.model.SessionType;
import com.notification.service.model.TaskStatus;
import com.notification.service.repository.TaskRepository;
import com.notification.service.telegram.TelegramNotificationBot;
import com.notification.service.telegram_interactive.handler.InteractiveHandler;
import com.notification.service.telegram_interactive.model.ThresholdModel;
import com.notification.service.telegram_interactive.model.session.ThresholdCreationSession;
import com.notification.service.telegram_interactive.service.ThresholdSessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ThresholdCreationHandler implements InteractiveHandler {

    private final ThresholdSessionService thresholdSessionService;
    private final TaskRepository taskRepository;

    @Override
    public SessionType getSessionType() {
        return SessionType.THRESHOLD_CREATING;
    }

    @Override
    public boolean isSessionInProgress(String chatId) {
        ThresholdCreationSession session = thresholdSessionService.getSession(chatId);
        return session != null && session.getStep() != ThresholdCreationSession.Step.COMPLETE;
    }

    @Override
    public void processCallback(TelegramNotificationBot bot, String chatId, String input) {
        ThresholdCreationSession session = thresholdSessionService.getOrCreateSession(chatId);
        ThresholdModel thresholdModel = session.getThresholdModel();

        switch (session.getStep()) {
            case SELECT_MERGE_REQUEST -> {
                Long selectedTaskId = Long.parseLong(input);
                Task selectedTask = taskRepository.findById(selectedTaskId)
                        .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + selectedTaskId));

                thresholdModel.setMrTitle(selectedTask.getTitle());
                thresholdModel.setLinkToMr(selectedTask.getLinkToMr());

                String message = String.format("""
                        Название: %s
                        Ссылка: %s
                        """, thresholdModel.getMrTitle(), thresholdModel.getLinkToMr());

                bot.handleInteractiveCallback(
                        chatId,
                        message,
                        selectedTaskId,
                        NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE,
                        "Уведомить ревьюера"
                );
                session.setStep(ThresholdCreationSession.Step.COMPLETE);
            }
        }
    }

    @Override
    public void cancelSession(String chatId) {
        thresholdSessionService.clearSession(chatId);
    }

    @Override
    public void startSession(TelegramNotificationBot bot, String chatId) {
        ThresholdCreationSession startedSession = thresholdSessionService.getOrCreateSession(chatId);
        startedSession.setStep(ThresholdCreationSession.Step.SELECT_MERGE_REQUEST);

        List<Task> tasks = taskRepository.findAllByStatus(TaskStatus.REVIEW)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with status - REVIEW"));

        createMergeRequestsChooseButtons(bot, chatId, tasks, "Выберите МР для создания Threshold:");
    }

    private void createMergeRequestsChooseButtons(TelegramNotificationBot bot, String chatId,
                                                  List<Task> tasks, String action) {
        List<List<InlineKeyboardButton>> buttons = tasks.stream()
                .map(task -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(String.format("TITLE: %s | LINK: %s", task.getTitle(), task.getLinkToMr()));
                    button.setCallbackData(String.valueOf(task.getId()));
                    return List.of(button);
                })
                .toList();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);

        SendMessage sendMessage = new SendMessage(chatId, action);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        bot.executeMessage(sendMessage);
    }
}
