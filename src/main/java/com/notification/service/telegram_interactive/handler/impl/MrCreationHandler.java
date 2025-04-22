package com.notification.service.telegram_interactive.handler.impl;

import com.notification.service.dto.TaskDto;
import com.notification.service.entity.User;
import com.notification.service.model.SessionType;
import com.notification.service.service.UserService;
import com.notification.service.telegram.TelegramNotificationBot;
import com.notification.service.telegram_interactive.WebhookService;
import com.notification.service.telegram_interactive.handler.InteractiveHandler;
import com.notification.service.telegram_interactive.model.MrCreationSession;
import com.notification.service.telegram_interactive.model.MrModel;
import com.notification.service.telegram_interactive.service.MrSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MrCreationHandler implements InteractiveHandler {

    private final UserService userService;
    private final WebhookService webhookService;
    private final MrSessionService mrSessionService;

    @Override
    public void processCallback(TelegramNotificationBot bot, String chatId, String input) {
        MrCreationSession session = mrSessionService.getOrCreateSession(chatId);
        MrModel sessionMrModel = session.getMrModel();
        List<User> reviewers = userService.findAllByReviewerRole();
        List<User> developers = userService.findAllByDeveloperRole();

        switch (session.getStep()) {
            case TITLE -> {
                sessionMrModel.setTitle(input);
                session.setStep(MrCreationSession.Step.LINK);
                bot.executeMessage(chatId, "Введите ссылку на МР:");
            }
            case LINK -> {
                sessionMrModel.setLinkToMr(input);
                session.setStep(MrCreationSession.Step.SELECT_REVIEWER);
                createUserChooseButtons(bot, chatId, reviewers, "Выберите ревьюера:");
            }
            case SELECT_REVIEWER -> {
                sessionMrModel.setReviewerId(Long.valueOf(input));
                session.setStep(MrCreationSession.Step.SELECT_DEVELOPER);
                createUserChooseButtons(bot, chatId, developers, "Выберите девелопера:");
            }
            case SELECT_DEVELOPER -> {
                sessionMrModel.setDeveloperId(Long.valueOf(input));
                session.setStep(MrCreationSession.Step.COMPLETE);
                createMergeRequest(bot, chatId, sessionMrModel);
                mrSessionService.clearSession(chatId);
            }
            default -> bot.executeMessage(chatId, "Что-то пошло не так. Попробуйте заново.");
        }
    }

    @Override
    public void startSession(TelegramNotificationBot bot, String chatId) {
        MrCreationSession startedSession = mrSessionService.getOrCreateSession(chatId);
        startedSession.setStep(MrCreationSession.Step.TITLE);
        bot.executeMessage(chatId, "Введите название МР:");
    }

    @Override
    public boolean isSessionInProgress(String chatId) {
        MrCreationSession session = mrSessionService.getSession(chatId);
        return session != null && session.getStep() != MrCreationSession.Step.COMPLETE;
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.MR_CREATION;
    }

    private void createUserChooseButtons(TelegramNotificationBot bot, String chatId, List<User> users, String action) {
        List<List<InlineKeyboardButton>> buttons = users.stream()
                .map(user -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(user.getUsername());
                    button.setCallbackData(String.valueOf(user.getId()));
                    return List.of(button);
                })
                .toList();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);

        SendMessage sendMessage = new SendMessage(chatId, action);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        bot.executeMessage(sendMessage);
    }

    public void createMergeRequest(TelegramNotificationBot bot, String chatId, MrModel model) {
        try {
            TaskDto createdTask = webhookService.createMergeRequest(model);
            bot.executeMessage(chatId, "Вы успешно создали МР: " + createdTask.getTitle());
        } catch (Exception e) {
            log.debug("Error creating merge request", e);
            bot.executeMessage(chatId, "Ошибка при создании MR. Попробуйте снова.");
        }
    }
}