package com.notification.service.telegram;


import com.notification.service.entity.User;
import com.notification.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HiveNotificationBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;

    private final UserService userService;

    private final String START = "/start";

    public HiveNotificationBot(@Value("${bot.token}") String botToken, UserService userService) {
        super(botToken);
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {

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

    public void sendMessageWithConfirmation(String chatId, String text, Long taskId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Уведомить ревьюера");
        button.setCallbackData("confirm_reviewer:" + taskId);

        keyboard.add(List.of(button));
        markup.setKeyboard(keyboard);

        SendMessage message = new SendMessage(chatId, text);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void startCommand(Update update) {
        String text = """
                Добро пожаловать в бот, %s.
                Здесь можно увидеть список МР.
                                
                Команды для использования:
                /start - запуск бота
                """;

        String formattedText = String.format(
                text, update.getMessage().getChat().getFirstName()
        );

        sendMessage(
                String.valueOf(update.getMessage().getChatId()),
                formattedText
        );

        String telegramUserName = update.getMessage().getChat().getUserName();
        Long telegramChatId = update.getMessage().getChat().getId();

        User userByTelegramUsername = userService.findUserByTelegramUsername(telegramUserName);
        if (userByTelegramUsername.getTelegramChatId() == 0) {
            userService.updateUserDataByTelegramUsername(telegramUserName, telegramChatId);
        }
    }

    private void defaultCommand(String chatId) {
        String formattedText = """
                Команды для использования:
                /start - запуск бота
                """;

        sendMessage(chatId, formattedText);
    }

    public void sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
