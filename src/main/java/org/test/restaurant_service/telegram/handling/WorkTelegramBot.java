package org.test.restaurant_service.telegram.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.test.restaurant_service.dto.response.WaiterCallRequestDTO;
import org.test.restaurant_service.service.impl.*;
import org.test.restaurant_service.telegram.config.BotConfig;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WorkTelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final StaffSendingOrderService staffSendingOrderService;

    public WorkTelegramBot(BotConfig botConfig, StaffSendingOrderService staffSendingOrderService) {
        this.botConfig = botConfig;
        this.staffSendingOrderService = staffSendingOrderService;
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/on", "Принять смену"));
        botCommands.add(new BotCommand("/off", "Выйти из смены"));
        botCommands.add(new BotCommand("/help", "Список доступных команд"));
        botCommands.add(new BotCommand("/info", "Информация о боте"));
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Не удалось обновить команды: {}", e.getMessage(), e);
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
        switch (text) {
            case "/on":
                handleStaffState(update, true);
                break;
            case "/off":
                handleStaffState(update, false);
                break;
            case "/help":
                sendHelp(update);
                break;
            case "/info":
                sendInfo(update);
                break;
            default:
                sendMessage(update.getMessage().getChatId(), "Неизвестная команда. Введите /help для списка доступных команд.");
                break;
        }
        log.debug("Received command '{}' from chatId={}", text, update.getMessage().getChatId());
    }
    private void sendInfo(Update update) {
        Long chatId = update.getMessage().getChatId();
        String infoText = String.format(
                "<b>Информация о боте:</b>\n"
                        + "Имя бота: %s\n"
                        + "Chat ID: %d\n"
                        + "Описание: бот для управления сменами персонала и уведомлений при вызове официанта",
                getBotUsername(), chatId
        );
        sendMessage(chatId, infoText);
    }

    private void handleStaffState(Update update, boolean state) {
        Long chatId = update.getMessage().getChatId();
        staffSendingOrderService.setStaffSendingState(chatId, state);

        String text = state
                ? "✅ Вы приняли смену! Теперь вы будете получать уведомления при вызовах официанта."
                : "❌ Вы вышли из смены! Теперь вы не получаете уведомлений.";

        sendMessage(chatId, text);
    }


    private void sendHelp(Update update) {
        String helpText = "<b>Доступные команды:</b>\n"
                + "/on — принять смену и получать уведомления о вызове официанта\n"
                + "/off — выйти из смены и отключить уведомления\n"
                + "/help — показать этот справочник по командам\n"
                + "/info — получить информацию о боте";
        sendMessage(update.getMessage().getChatId(), helpText);
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


}
