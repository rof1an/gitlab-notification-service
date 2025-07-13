package com.notification.service.bot.interactive;

import com.notification.service.bot.TelegramNotificationBot;
import com.notification.service.bot.interactive.handler.InteractiveHandler;
import com.notification.service.bot.util.TelegramButtonLabels;
import com.notification.service.bot.util.TelegramKeyboardFactory;
import com.notification.service.core.model.SessionType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TelegramInteractiveManager {

    private final Map<SessionType, InteractiveHandler> handlersMap;

    public TelegramInteractiveManager(List<InteractiveHandler> handlers) {
        this.handlersMap = handlers.stream()
                .collect(Collectors.toMap(InteractiveHandler::getSessionType, handler -> handler));
    }

    public void showMenu(TelegramNotificationBot bot, String chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Выберите действие:")
                .replyMarkup(TelegramKeyboardFactory.createMainMenuKeyboard())
                .build();

        bot.executeMessage(message);
    }

    public void handleAction(SessionType sessionType, TelegramNotificationBot bot, String chatId) {
        InteractiveHandler handler = handlersMap.get(sessionType);
        if (handler != null) {
            handler.startSession(bot, chatId);
        }
    }

    public boolean isSessionInProgress(String chatId) {
        return handlersMap.values().stream()
                .anyMatch(handler -> handler.isSessionInProgress(chatId));
    }

    public void cancelCurrentSession(String chatId) {
        handlersMap.values().forEach(handler -> {
            if (handler.isSessionInProgress(chatId)) {
                handler.cancelSession(chatId);
            }
        });
    }

    public void processCallback(TelegramNotificationBot bot, String chatId, String callbackData) {
        if (callbackData.equalsIgnoreCase(TelegramButtonLabels.CANCEL_ACTION)) {
            cancelCurrentSession(chatId);
            bot.executeMessage(chatId, "Действие отменено.");
            return;
        }

        handlersMap.values().stream()
                .filter(handler -> handler.isSessionInProgress(chatId))
                .findFirst()
                .ifPresent(handler -> handler.processCallback(bot, chatId, callbackData));
    }
}
