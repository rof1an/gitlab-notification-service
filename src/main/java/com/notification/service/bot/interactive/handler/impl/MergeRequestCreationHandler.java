package com.notification.service.bot.interactive.handler.impl;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.bot.interactive.handler.InteractiveHandler;
import com.notification.service.bot.interactive.model.MergeRequestModel;
import com.notification.service.bot.interactive.model.session.MergeRequestCreationSession;
import com.notification.service.bot.interactive.service.WebhookService;
import com.notification.service.bot.interactive.service.sessionService.MergeRequestCreationSessionService;
import com.notification.service.bot.util.TelegramKeyboardFactory;
import com.notification.service.core.dto.TaskDto;
import com.notification.service.core.entity.User;
import com.notification.service.core.model.SessionType;
import com.notification.service.core.service.UserService;
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
    private final MergeRequestCreationSessionService sessionService;

    @Override
    public void processCallback(TelegramNotificationBot bot, String chatId, String input) {
        MergeRequestCreationSession session = sessionService.getOrCreateSession(chatId);
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
            }
            default -> bot.executeMessage(chatId, "Что-то пошло не так. Попробуйте заново.");
        }
    }

    @Override
    public void cancelSession(String chatId) {
        sessionService.clearSession(chatId);
    }

    @Override
    public void startSession(TelegramNotificationBot bot, String chatId) {
        MergeRequestCreationSession startedSession = sessionService.getOrCreateSession(chatId);
        startedSession.setStep(MergeRequestCreationSession.Step.TITLE);
        bot.executeMessage(chatId, "Введите название МР:");
    }

    @Override
    public boolean isSessionInProgress(String chatId) {
        MergeRequestCreationSession session = sessionService.getSession(chatId);
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
            bot.executeMessage(chatId, "Ошибка при создании MР. Попробуйте снова.");
        }
    }
}