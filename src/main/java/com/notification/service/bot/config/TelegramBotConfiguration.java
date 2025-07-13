package com.notification.service.bot.config;

import com.notification.service.bot.TelegramNotificationBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramNotificationBot telegramNotificationBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramNotificationBot);
        return api;
    }
}