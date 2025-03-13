package com.notification.service.telegram;

import com.notification.service.entity.Task;
import com.notification.service.entity.User;
import com.notification.service.service.TaskService;
import com.notification.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HiveNotificationBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;

    private final TaskService taskService;

    private final UserService userService;

    private final String START = "/start";
    private final String GET_REPLY_BUTTONS = "/get_reply_buttons";
    private final String GET_INLINE_BUTTONS = "/get_inline_buttons";
    private final String GET_NEW_MR = "/get_new_mr";

    public HiveNotificationBot(@Value("${bot.token}") String botToken, UserService userService, TaskService taskService) {
        super(botToken);
        this.userService = userService;
        this.taskService = taskService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Update object = {}", update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            switch (message) {
                case START -> {
                    startCommand(update);
                }
                case GET_REPLY_BUTTONS -> {
                    sendReplyKeyboard(chatId);
                }
                case GET_INLINE_BUTTONS -> {
                    sendInlineKeyboard(chatId);
                }
                case GET_NEW_MR -> {
                    String userName = update.getMessage().getChat().getUserName();
                    getNewMr(userName);
                }
                case "Кнопка 1" -> {
                    sendMessage(chatId, "Вы нажали кнопку 1");
                }
                case "Кнопка 2" -> {
                    sendMessage(chatId, "Вы нажали кнопку 2");
                }
                case "Кнопка 3" -> {
                    sendMessage(chatId, "Вы нажали кнопку 3");
                }
                default -> {
                    defaultCommand(chatId);
                }
            }
        }
    }

    private Task getNewMr(String username){
        User userByTelegramUsername = userService.findUserByTelegramUsername(username);
        Task task = taskService.getTaskByUser(userByTelegramUsername.getId(), userByTelegramUsername.getRole());
        return task;
    }

    private ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Кнопка 1");
        row1.add("Кнопка 2");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Кнопка 3");

        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    private void sendReplyKeyboard(String chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = createReplyKeyboardMarkup();

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите действие");
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendInlineKeyboard(String chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Нажми меня");
        button1.setCallbackData("button_pressed");

        row.add(button1);
        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage(chatId, "Выберите действие:");
        message.setReplyMarkup(inlineKeyboardMarkup);

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
                /get_reply_buttons
                /get_inline_buttons
                """;

        String formattedText = String.format(
                text, update.getMessage().getChat().getFirstName()
        );

        sendMessage(
                String.valueOf(update.getMessage().getChatId()), formattedText
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
                /get_reply_buttons
                /get_inline_buttons
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