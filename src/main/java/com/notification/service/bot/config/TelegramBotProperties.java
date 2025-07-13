package com.notification.service.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bot")
public class TelegramBotProperties {
    private String token;
    private String name;
}
