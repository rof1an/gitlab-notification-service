package com.notification.service.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.service.service.NotificationService;
import com.notification.service.service.TaskService;
import com.notification.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class HiveNotificationBot extends TelegramLongPollingBot {

    private String botUsername;

    private final UserService userService;

    private final NotificationService notificationService;

    private final TaskService taskService;

    private final ObjectMapper objectMapper;

    public HiveNotificationBot(@Value("${bot.name}") String botUsername,
                               UserService userService,
                               NotificationService notificationService,
                               TaskService taskService,
                               ObjectMapper objectMapper) {
        this.botUsername = botUsername;
        this.userService = userService;
        this.taskService = taskService;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Update object = {}", update);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
