package com.notification.service.telegram;


import com.notification.service.entity.User;
import com.notification.service.model.NotificationType;
import com.notification.service.service.UserService;
import com.notification.service.telegram.handler.CallbackNotificationHandler;
import com.notification.service.util.TelegramKeyboardFactory;
import com.notification.service.util.TelegramMessageFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.notification.UnableToSendNotificationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
public class HiveNotificationBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;

    private final TelegramMessageFormatter telegramMessageFormatter;

    private final TelegramKeyboardFactory keyboardFactory;

    private final UserService userService;

    private final List<CallbackNotificationHandler> callbackHandlers;

    private final String START = "/start";

    public HiveNotificationBot(@Value("${bot.token}") String botToken,
                               TelegramMessageFormatter telegramMessageFormatter,
                               TelegramKeyboardFactory keyboardFactory,
                               List<CallbackNotificationHandler> callbackHandlers, UserService userService) {
        super(botToken);
        this.telegramMessageFormatter = telegramMessageFormatter;
        this.keyboardFactory = keyboardFactory;
        this.callbackHandlers = callbackHandlers;
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();

            switch (message) {
                case START -> {
                    startCommand(update);
                }
                default -> {
                    defaultCommand(update.getMessage().getChatId().toString());
                }
            }
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        String[] dataParts = callbackQuery.getData().split(":");
        if (dataParts.length < 2) {
            log.warn("Invalid callback data: {}", callbackQuery.getData());
            return;
        }

        NotificationType incomingType = NotificationType.valueOf(dataParts[0]);
        Long incomingTaskId = Long.parseLong(dataParts[1]);

        callbackHandlers.stream()
                .filter(handler -> handler.getNotificationType() == incomingType)
                .findFirst()
                .ifPresentOrElse(
                        handler -> handler.handle(this, callbackQuery, incomingTaskId),
                        () -> log.warn("No handler found for type: {}", incomingType)
                );

        answerCallback(callbackQuery);
    }

    public void handleSendDeveloperNewTaskMessageWithConfirmation(String chatId, String text, Long taskId) {
        sendInteractiveMessage(
                chatId,
                text,
                taskId,
                NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE,
                "Уведомить ревьюера"
        );
    }

    public void handleSendReviewerThresholdAccept(String chatId, String text, Long taskId) {
        sendInteractiveMessage(
                chatId,
                text,
                taskId,
                NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE,
                "Уведомить ревьюера"
        );
    }

    public void handleSendDeveloperNewFixOnThresholdAccept(String chatId, String text, Long taskId) {
        sendInteractiveMessage(
                chatId,
                text,
                taskId,
                NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE,
                "Уведомить ревьюера"
        );
    }

    private void sendInteractiveMessage(String chatId, String text, Long taskId, NotificationType type, String buttonText) {
        InlineKeyboardMarkup markup = keyboardFactory.createSingleButtonKeyboard(buttonText, type, taskId);
        SendMessage message = new SendMessage(chatId, text);
        message.setReplyMarkup(markup);
        executeMessage(message);
    }

    private void startCommand(Update update) {
        executeMessage(
                String.valueOf(update.getMessage().getChatId()),
                telegramMessageFormatter.startCommand(update)
        );

        String telegramUserName = update.getMessage().getChat().getUserName();
        Long telegramChatId = update.getMessage().getChat().getId();

        User userByTelegramUsername = userService.findUserByTelegramUsername(telegramUserName);
        if (userByTelegramUsername.getTelegramChatId() == 0) {
            userService.updateUserDataByTelegramUsername(telegramUserName, telegramChatId);
        }
    }

    private void answerCallback(CallbackQuery callbackQuery) {
        try {
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            execute(answer);
        } catch (TelegramApiException e) {
            log.error("Failed to answer callback", e);
        }
    }

    private void defaultCommand(String chatId) {
        String formattedText = telegramMessageFormatter.defaultCommand();
        executeMessage(chatId, formattedText);
    }

    private void executeMessage(BotApiMethod<?> method) {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
            throw new UnableToSendNotificationException("Ошибка Telegram API", e);
        }
    }

    public void executeMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
            throw new UnableToSendNotificationException("Ошибка Telegram API", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
