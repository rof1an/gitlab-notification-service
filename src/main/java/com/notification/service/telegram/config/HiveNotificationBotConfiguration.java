package com.notification.service.telegram.config;

import com.notification.service.telegram.HiveNotificationBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class HiveNotificationBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(HiveNotificationBot hiveNotificationBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(hiveNotificationBot);
        return api;
    }
}