package com.notification.service.util;

import com.notification.service.model.NotificationType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class TelegramKeyboardFactory {

    public InlineKeyboardMarkup createSingleButtonKeyboard(String buttonText, NotificationType type, Long taskId) {
        InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
        button.setCallbackData(type + ":" + taskId);
        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }
}
