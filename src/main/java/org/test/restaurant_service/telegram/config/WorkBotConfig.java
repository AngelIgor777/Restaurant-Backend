package org.test.restaurant_service.telegram.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class WorkBotConfig extends BotConfig {

    @Value("${telegram.workBot.name}")
    private String workBotName;

    @Value("${telegram.workBot.key}")
    private String workBotKey;
}
