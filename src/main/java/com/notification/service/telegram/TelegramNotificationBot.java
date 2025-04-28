package com.notification.service.telegram;


import com.notification.service.entity.User;
import com.notification.service.model.NotificationType;
import com.notification.service.service.UserService;
import com.notification.service.telegram.config.TelegramBotProperties;
import com.notification.service.telegram.handler.CallbackNotificationHandler;
import com.notification.service.util.TelegramKeyboardFactory;
import com.notification.service.util.TelegramMessageFormatter;
import lombok.extern.slf4j.Slf4j;
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
public class TelegramNotificationBot extends TelegramLongPollingBot {

    @Value("${bot.whitelist.ids}")
    private List<Long> whitelistIds;

    private final TelegramBotProperties telegramBotProperties;
    private final List<CallbackNotificationHandler> callbackHandlers;
    private final TelegramMessageFormatter telegramMessageFormatter;
    private final TelegramKeyboardFactory keyboardFactory;
    private final UserService userService;
    private final String START = "/start";

    public TelegramNotificationBot(TelegramBotProperties telegramBotProperties,
                                   UserService userService,
                                   TelegramMessageFormatter telegramMessageFormatter,
                                   TelegramKeyboardFactory keyboardFactory,
                                   List<CallbackNotificationHandler> callbackHandlers) {
        super(telegramBotProperties.getToken());
        this.telegramBotProperties = telegramBotProperties;
        this.userService = userService;
        this.telegramMessageFormatter = telegramMessageFormatter;
        this.keyboardFactory = keyboardFactory;
        this.callbackHandlers = callbackHandlers;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (whitelistIds.contains(update.getMessage().getFrom().getId())) {
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
        } else {
            deniedAccessCommand(update);
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        String[] dataParts = parseCallbackDataParts(callbackQuery.getData());
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

    public void handleInteractiveCallback(String chatId, String text, Long taskId, NotificationType type, String buttonText) {
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

    private void deniedAccessCommand(Update update) {
        executeMessage(
                String.valueOf(update.getMessage().getFrom().getId()),
                "У вас нет доступа к боту."
        );
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

    // то, что до ":" - тип из NotificationType
    // то, что после ":" - айди Task
    private String[] parseCallbackDataParts(String callbackData) {
        String[] dataParts = callbackData.split(":");
        if (dataParts.length < 2) {
            log.warn("Invalid callback data: {}", callbackData);
            throw new IllegalArgumentException("Callback data must contain at least type and taskId");
        }
        return dataParts;
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
        return telegramBotProperties.getName();
    }
}
