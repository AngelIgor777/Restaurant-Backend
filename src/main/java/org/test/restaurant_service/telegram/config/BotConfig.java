package org.test.restaurant_service.telegram.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfig {
    @Value("${telegram.bot.name}")
    String botName;

    @Value("${telegram.bot.key}")
    String botKey;

    @Value("${telegram.workBot.name}")
    String WorkBotName;

    @Value("${telegram.workBot.key}")
    String WorkBotKey;
}
