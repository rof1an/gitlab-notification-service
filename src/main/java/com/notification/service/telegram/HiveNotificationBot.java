package com.notification.service.telegram;


import com.notification.service.entity.Task;
import com.notification.service.entity.User;
import com.notification.service.model.NotificationType;
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

    private void handleCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        String sendDeveloperNewMr = String.valueOf(NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE);
        String sendReviewerThreshold = String.valueOf(NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE);
        String sendDeveloperThresholdNewFix = String.valueOf(NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE);

        if (data.startsWith(sendDeveloperNewMr)) {
            String[] parts = data.split(":");

            if (parts.length > 1) {
                Long taskId = Long.parseLong(parts[1]);
                handleNewMrDeveloperConfirmation(callbackQuery, taskId);
            }
        } else if (data.startsWith(sendReviewerThreshold)) {
            String[] parts = data.split(":");

            if (parts.length > 1) {
                Long taskId = Long.parseLong(parts[1]);
                handleAcceptThresholdReviewerConfirmation(callbackQuery, taskId);
            }
        } else if (data.startsWith(sendDeveloperThresholdNewFix)) {
            String[] parts = data.split(":");

            if (parts.length > 1) {
                Long taskId = Long.parseLong(parts[1]);
                handleSendReviewerNewFixOnThresholdConfirmation(callbackQuery, taskId);
            }
        }

        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());

        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка при ответе на callback", e);
        }
    }

    public void sendDeveloperNewTaskMessageWithConfirmation(String chatId, String text, Long taskId) {
        String buttonText = "Уведомить ревьюера";
        InlineKeyboardMarkup markup =
                createInlineKeyboardMarkup(buttonText, NotificationType.SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE, taskId);

        SendMessage message = new SendMessage(chatId, text);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    public void sendReviewerThresholdAccept(String chatId, String text, Long taskId) {
        String buttonText = "Уведомить девелопера";
        InlineKeyboardMarkup markup =
                createInlineKeyboardMarkup(buttonText, NotificationType.SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE, taskId);

        SendMessage message = new SendMessage(chatId, text);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void handleNewMrDeveloperConfirmation(CallbackQuery callbackQuery, Long taskId) {
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

    private void handleAcceptThresholdReviewerConfirmation(CallbackQuery callbackQuery, Long taskId) {
        Task task = taskService.getTaskById(taskId);
        String developerChatId = String.valueOf(task.getDeveloper().getTelegramChatId());

        String messageText = String.format("""
                        Новый threshold в МР по задаче: %s
                        Нужны исправления.
                        Ссылка на Merge Request: %s
                        Developer: %s
                        Reviewer: %s
                        """,
                task.getTitle(),
                task.getLinkToMr(),
                task.getDeveloper().getUsername(),
                task.getReviewer().getUsername()
        );

        sendMessage(developerChatId, messageText);
        taskService.updateTaskStatus(taskId, TaskStatus.NEED_FIXES);

        sendMessage(
                callbackQuery.getMessage().getChatId().toString(),
                "Девелопер уведомлен! ✅"
        );
    }

    public void handleSendDeveloperNewFixOnThresholdAccept(String chatId, String text, Long taskId) {
        String buttonText = "Уведомить ревьюера";
        InlineKeyboardMarkup markup =
                createInlineKeyboardMarkup(buttonText, NotificationType.SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE, taskId);

        SendMessage message = new SendMessage(chatId, text);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void handleSendReviewerNewFixOnThresholdConfirmation(CallbackQuery callbackQuery, Long taskId) {
        Task task = taskService.getTaskById(taskId);
        String developerChatId = String.valueOf(task.getReviewer().getTelegramChatId());

        String messageText = String.format("""
                        Новое изменение по threshold в МР по задаче: %s
                        Ссылка на Merge Request: %s
                        Developer: %s
                        Reviewer: %s
                        """,
                task.getTitle(),
                task.getLinkToMr(),
                task.getDeveloper().getUsername(),
                task.getReviewer().getUsername()
        );

        sendMessage(developerChatId, messageText);
        taskService.updateTaskStatus(taskId, TaskStatus.REVIEW);

        sendMessage(
                callbackQuery.getMessage().getChatId().toString(),
                "Ревьюер уведомлен! ✅"
        );
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(String buttonText, NotificationType notificationType, Long taskId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(buttonText);
        button.setCallbackData(notificationType + ":" + taskId);
        keyboard.add(List.of(button));
        markup.setKeyboard(keyboard);

        return markup;
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
