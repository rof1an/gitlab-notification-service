package com.notification.service.telegram_interactive.handler;

import com.notification.service.model.SessionType;
import com.notification.service.telegram.TelegramNotificationBot;

public interface InteractiveHandler {

    SessionType getSessionType();

    boolean isSessionInProgress(String chatId);

    void processCallback(TelegramNotificationBot bot, String chatId, String callbackData);

    void startSession(TelegramNotificationBot bot, String chatId);
}
