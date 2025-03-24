package com.notification.service.telegram;


import com.notification.service.entity.Task;
import com.notification.service.entity.User;
import com.notification.service.model.TaskStatus;
import com.notification.service.service.TaskService;
import com.notification.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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

    private final TaskService taskService;

    private final UserService userService;

    private final String START = "/start";

    public HiveNotificationBot(@Value("${bot.token}") String botToken, UserService userService, TaskService taskService) {
        super(botToken);
        this.userService = userService;
        this.taskService = taskService;
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

    public void sendDeveloperNewTaskMessageWithConfirmation(String chatId, String text, Long taskId) {
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

    private void handleCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        if (data.startsWith("confirm_reviewer:")) {
            Long taskId = Long.parseLong(data.split(":")[1]);
            handleReviewerConfirmation(callbackQuery, taskId);
        }

        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());

        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка при ответе на callback", e);
        }
    }

    private void handleReviewerConfirmation(CallbackQuery callbackQuery, Long taskId) {
        Task task = taskService.getTaskById(taskId);
        String reviewerChatId = String.valueOf(task.getReviewer().getTelegramChatId());

        String messageText = String.format("""
                        Новый MR на проверку: %s
                        Ссылка на Merge Request: %s
                        Developer: %s
                        Reviewer: %s
                        """,
                task.getTitle(),
                task.getLinkToMr(),
                task.getDeveloper().getUsername(),
                task.getReviewer().getUsername()
        );

        sendMessage(reviewerChatId, messageText);
        taskService.updateTaskStatus(taskId, TaskStatus.REVIEW);

        sendMessage(
                callbackQuery.getMessage().getChatId().toString(),
                "Ревьюер уведомлен! ✅"
        );
    }

    public void sendDeveloperMergedTakNotification(String chatId, String text){
        sendMessage(chatId, text);
    }

    private void startCommand(Update update) {
        String formattedText = String.format("""
                        Добро пожаловать в бот, %s.
                        Здесь можно увидеть список МР.
                                        
                        Команды для использования:
                        /start - запуск бота
                        """,
                update.getMessage().getChat().getFirstName()
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
