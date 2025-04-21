package org.test.restaurant_service.telegram.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.test.restaurant_service.dto.response.WaiterCallRequestDTO;
import org.test.restaurant_service.service.impl.*;
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
                handleStaffState(update, true);
                break;
            case "/off":
                handleStaffState(update, false);
                break;
            default:
                sendMessage(update.getMessage().getChatId(), "Привет!");
                break;
        }
        log.debug("Receive chatId for {}", chatId);
    }

    private void handleStaffState(Update update, boolean state) {
        Long chatId = update.getMessage().getChatId();
        staffSendingOrderService.setStaffSendingState(chatId, state);
        String text = null;
        if (state) {
            text = "Вы приняли смену! Теперь вы будете получать уведомления при вызовах официанта!";
        } else if (!false) {
            text = "Вы вышли из смены! Теперь вы не получаете уведомлений!";
        }
        createAndSendMessage(update, text);
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
        botCommands.add(new BotCommand("/on", "Принять смену"));
        botCommands.add(new BotCommand("/off", "Выйти из заказы"));
        botCommands.add(new BotCommand("/help", "Список доступных команд"));
        botCommands.add(new BotCommand("/info", "Информация о боте"));
        return botCommands;
    }

    private void createAndSendMessage(Update update, String text, InlineKeyboardMarkup keyboard) {
        SendMessage editMessageText = getSendMessage(update, text);
        editMessageText.setReplyMarkup(keyboard);
        executeMessage(editMessageText);
    }

    private void createAndSendMessage(Update update, String text) {
        SendMessage editMessageText = getSendMessage(update, text);
        executeMessage(editMessageText);
    }

    private SendMessage getSendMessage(Update update, String text) {
        return getSendMessage(String.valueOf(update.getMessage().getChatId()), text);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private SendMessage getSendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("HTML");
        return message;
    }

    public void sendWaiterRequestToStaff(String caption, Long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setCaption(caption);
        sendPhoto.setParseMode("HTML");
    }

}
