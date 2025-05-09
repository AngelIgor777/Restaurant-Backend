package org.test.restaurant_service.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.test.restaurant_service.telegram.handling.TelegramBot;
import org.test.restaurant_service.telegram.handling.WorkTelegramBot;

@Component
@RequiredArgsConstructor
public class BotInitializer {

    private final TelegramBot telegramBot;
    private final WorkTelegramBot workTelegramBot;


    @EventListener({ContextRefreshedEvent.class})
    public void initBot() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBot);
    }

    @EventListener({ContextRefreshedEvent.class})
    public void initWorkBot() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(workTelegramBot);
    }



}
