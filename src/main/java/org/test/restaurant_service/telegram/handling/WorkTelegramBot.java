package org.test.restaurant_service.telegram.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.test.restaurant_service.service.impl.StaffSendingOrderService;
import org.test.restaurant_service.telegram.config.BotConfig;

import java.util.ArrayList;

@Slf4j
@Component
public class WorkTelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final StaffSendingOrderService staffSendingOrderService;

    public WorkTelegramBot(BotConfig botConfig, StaffSendingOrderService staffSendingOrderService) {
        this.botConfig = botConfig;
        this.staffSendingOrderService = staffSendingOrderService;
        try {
            this.execute(new SetMyCommands(getCommands(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getWorkBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getWorkBotKey();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            handleTextCommand(update, text);
        }
    }

    private void handleTextCommand(Update update, String text) {
        Long chatId = update.getMessage().getChatId();
        switch (text) {
            case "/start":
                sendMessage(update.getMessage().getChatId(), "Привет!");
                break;
            case "/on":
                staffSendingOrderService.enableSending(chatId);
            default:
                sendMessage(update.getMessage().getChatId(), "Привет!");
                break;
        }
        log.debug("Receive chatId for {}", chatId);
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setParseMode("HTML");
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }

    private ArrayList<BotCommand> getCommands() {
        ArrayList<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "Запуск бота"));
        botCommands.add(new BotCommand("/on", "Принимать заказы"));
        botCommands.add(new BotCommand("/off", "Не принимать заказы"));
        botCommands.add(new BotCommand("/help", "Список доступных команд"));
        botCommands.add(new BotCommand("/info", "Информация о боте"));
        botCommands.add(new BotCommand("/about", "Показать мою информацию"));
        return botCommands;
    }

}
