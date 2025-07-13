package com.notification.service.bot.interactive.handler;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.core.model.SessionType;

public interface InteractiveHandler {

    SessionType getSessionType();

    boolean isSessionInProgress(String chatId);

    void processCallback(TelegramNotificationBot bot, String chatId, String input);

    void cancelSession(String chatId);

    void startSession(TelegramNotificationBot bot, String chatId);
}
