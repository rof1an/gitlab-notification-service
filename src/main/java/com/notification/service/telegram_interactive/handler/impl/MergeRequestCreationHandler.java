package com.notification.service.telegram_interactive.handler.impl;

import com.notification.service.dto.TaskDto;
import com.notification.service.entity.User;
import com.notification.service.model.SessionType;
import com.notification.service.service.UserService;
import com.notification.service.telegram.TelegramNotificationBot;
import com.notification.service.telegram_interactive.WebhookService;
import com.notification.service.telegram_interactive.handler.InteractiveHandler;
import com.notification.service.telegram_interactive.model.MergeRequestModel;
import com.notification.service.telegram_interactive.model.session.MergeRequestCreationSession;
import com.notification.service.telegram_interactive.service.MergeRequestSessionService;
import com.notification.service.util.TelegramKeyboardFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MergeRequestCreationHandler implements InteractiveHandler {

    private final UserService userService;
    private final WebhookService webhookService;
    private final MergeRequestSessionService mergeRequestSessionService;

    @Override
    public void processCallback(TelegramNotificationBot bot, String chatId, String input) {
        MergeRequestCreationSession session = mergeRequestSessionService.getOrCreateSession(chatId);
        MergeRequestModel sessionMergeRequestModel = session.getMergeRequestModel();
        List<User> reviewers = userService.findAllByReviewerRole();
        List<User> developers = userService.findAllByDeveloperRole();

        switch (session.getStep()) {
            case TITLE -> {
                sessionMergeRequestModel.setTitle(input);
                session.setStep(MergeRequestCreationSession.Step.LINK);
                bot.executeMessage(chatId, "Введите ссылку на МР:");
            }
            case LINK -> {
                sessionMergeRequestModel.setLinkToMr(input);
                session.setStep(MergeRequestCreationSession.Step.SELECT_REVIEWER);
                createUserChooseButtons(bot, chatId, reviewers, "Выберите ревьюера:");
            }
            case SELECT_REVIEWER -> {
                sessionMergeRequestModel.setReviewerId(Long.valueOf(input));
                session.setStep(MergeRequestCreationSession.Step.SELECT_DEVELOPER);
                createUserChooseButtons(bot, chatId, developers, "Выберите девелопера:");
            }
            case SELECT_DEVELOPER -> {
                sessionMergeRequestModel.setDeveloperId(Long.valueOf(input));
                session.setStep(MergeRequestCreationSession.Step.COMPLETE);
                createMergeRequest(bot, chatId, sessionMergeRequestModel);
                mergeRequestSessionService.clearSession(chatId);
            }
            default -> bot.executeMessage(chatId, "Что-то пошло не так. Попробуйте заново.");
        }
    }

    @Override
    public void cancelSession(String chatId) {
        mergeRequestSessionService.clearSession(chatId);
    }

    @Override
    public void startSession(TelegramNotificationBot bot, String chatId) {
        MergeRequestCreationSession startedSession = mergeRequestSessionService.getOrCreateSession(chatId);
        startedSession.setStep(MergeRequestCreationSession.Step.TITLE);
        bot.executeMessage(chatId, "Введите название МР:");
    }

    @Override
    public boolean isSessionInProgress(String chatId) {
        MergeRequestCreationSession session = mergeRequestSessionService.getSession(chatId);
        return session != null && session.getStep() != MergeRequestCreationSession.Step.COMPLETE;
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.MR_CREATION;
    }

    private void createUserChooseButtons(TelegramNotificationBot bot, String chatId, List<User> users, String action) {
        InlineKeyboardMarkup inlineKeyboardMarkup = TelegramKeyboardFactory.createSingleColumnKeyboard(
                users,
                User::getUsername,
                user -> String.valueOf(user.getId())
        );

        SendMessage sendMessage = new SendMessage(chatId, action);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        bot.executeMessage(sendMessage);
    }

    public void createMergeRequest(TelegramNotificationBot bot, String chatId, MergeRequestModel model) {
        try {
            TaskDto createdTask = webhookService.createMergeRequest(model);
            bot.executeMessage(chatId, "Вы успешно создали МР: " + createdTask.getTitle());
        } catch (Exception e) {
            log.debug("Error creating merge request", e);
            bot.executeMessage(chatId, "Ошибка при создании MR. Попробуйте снова.");
        }
    }
}