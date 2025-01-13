package org.test.restaurant_service.telegram.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.rabbitmq.producer.RabbitMQJsonProducer;
import org.test.restaurant_service.service.PhotoService;
import org.test.restaurant_service.service.impl.TelegramUserServiceImpl;
import org.test.restaurant_service.service.impl.PhotoServiceImpl;
import org.test.restaurant_service.service.impl.ProductServiceImpl;
import org.test.restaurant_service.service.impl.ProductTypeServiceImpl;
import org.test.restaurant_service.telegram.config.BotConfig;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramUserServiceImpl telegramUserService;
    private final ProductTypeServiceImpl productTypeService;
    private final ProductServiceImpl productService;
    private final RabbitMQJsonProducer rabbitMQJsonProducer;
    private final PhotoService photoService;

    private final String BUTTON_BACK_TO_MENU = "BACK_TO_MENU";

    private final String helpText =
            "📖 <b>Доступные команды:</b>\n\n" +
                    "🚀 /start - <i>Запуск бота</i>\n" +
                    "❓ /help - <i>Список доступных команд</i>\n" +
                    "ℹ️ /info - <i>Информация о боте</i>\n" +
                    "🍽️ /menu - <i>Показать меню</i>\n\n" +
                    "✨ Используйте команды, чтобы сделать ваше настроение более радостным!";
    private final String infoText =
            "🤖 <b>Добро пожаловать!</b>\n\n" +
                    "Этот бот создан, чтобы сделать вашу жизнь проще и приятнее! 🌟\n\n" +
                    "С его помощью вы можете:\n" +
                    "📝 Заказать еду на нашем сайте\n" +
                    "📢 Получать самые свежие новости о мероприятиях ARNAUT's\n\n" +
                    "✨ Мы всегда рады быть вам полезными!";
    private final BotConfig botConfig;

    private final ProductServiceImpl productServiceImpl;
    private final TelegramUserServiceImpl otpServiceImpl;
    private final PhotoServiceImpl photoServiceImpl;

    private List<String> callbackProductTypesData = new ArrayList<>();

    private List<String> callbackProductsData = new ArrayList<>();


    public TelegramBot(TelegramUserServiceImpl telegramUserService, ProductTypeServiceImpl productTypeService, ProductServiceImpl productService, RabbitMQJsonProducer rabbitMQJsonProducer, PhotoService photoService, BotConfig botConfig, ProductServiceImpl productServiceImpl, TelegramUserServiceImpl otpServiceImpl, PhotoServiceImpl photoServiceImpl) {
        this.telegramUserService = telegramUserService;
        this.productTypeService = productTypeService;
        this.productService = productService;
        this.rabbitMQJsonProducer = rabbitMQJsonProducer;
        this.photoService = photoService;

        this.botConfig = botConfig;
        ArrayList<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "Запуск бота"));
        botCommands.add(new BotCommand("/help", "Список доступных команд"));
        botCommands.add(new BotCommand("/info", "Информация о боте"));
        botCommands.add(new BotCommand("/menu", "Показать меню"));


        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        this.productServiceImpl = productServiceImpl;
        this.otpServiceImpl = otpServiceImpl;
        this.photoServiceImpl = photoServiceImpl;
    }

    @Override
    public String getBotUsername() {
        String botName = botConfig.getBotName();
        return botName;
    }

    @Override
    public String getBotToken() {
        String botKey = botConfig.getBotKey();
        return botKey;

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            Long chatId = update.getMessage().getChatId();
            switch (text) {
                case "/start":
                    registerFull(update);
                    break;
                case "/help":
                    sendHelpMessage(update);
                    break;
                case "/info":
                    sendMessage(update, infoText);
                    break;
                case "/menu":
                    menu(update);
                    break;
                case "/website":
                    sendMessageWithMarkdown(chatId,
                            String.format("🌟 Привет! Добро пожаловать на наш сайт: [arnauts.md](https://arnauts.md/%s). Мы рады, что вы с нами! 😊", chatId));
                    break;
                default:
                    sendMessageWithMarkdown(chatId, String.format("Неизвестная команда 🤯. Введите /help, чтобы увидеть доступные команды.\n\n" +
                            "Можете посмотреть наше меню /menu ☺ или сделать заказ у нас на сайте [arnauts.md](https://arnauts.md/%s).", chatId));
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);

        } else if (update.getMessage().hasSticker()) {
            stickerHandler(update);
        }
    }
//
//    @Scheduled(cron = "${cron.scheduler}")
//    private void sendAds() {
//        String adText =3333
//                "🍽️ <b>Время вкусных открытий!</b>\n\n" +
//                        "✨ Сегодня у нас для вас нечто особенное:\n" +
//                        "🍕 <b>Пицца недели:</b> Сырный взрыв — только 149 mdl!\n" +
//                        "🍹 <b>Коктейли:</b> Закажи два и получи третий в подарок!\n\n" +
//                        "🎉 <i>Забронируйте столик прямо сейчас, чтобы не упустить шанс насладиться уникальными блюдами!</i>\n\n" +
//                        "📲 Нажмите /menu, чтобы посмотреть всё меню!\n\n" +
//                        "❤️ С любовью, ваш ARNAUT's!";
//
//        List<Otp> all = otpServiceImpl.getAll();
//        for (Otp otp : all) {
//            prepareAndSendMessage(otp.getChatId(), adText);
//        }
//    }

//    @Scheduled(cron = "${cron.scheduler}")
//    public void sendPhotoWithCaption() throws IOException {
//
//
//        SendPhoto photo = new SendPhoto();
//        Resource image = photoService.getImage("pizza.png");
//        File file = image.getFile();
//        photo.setPhoto(new InputFile(file));
//        photo.setParseMode("HTML");
//
//        List<TelegramUserEntity> all = otpServiceImpl.getAll();
//        for (TelegramUserEntity telegramUserEntity : all) {
//
//            photo.setCaption(String.format("%s Привет! 😎 У нас для тебя что-то новенькое!\n\n" +
//                    "🍽️<b>Время вкусных открытий!</b>\n\n" +
//                    "✨ Сегодня у нас для вас нечто особенное:\n" +
//                    "🍕 <b>Пицца недели:</b> Сырный взрыв — только 149 лей!\n" +
//                    "🎉 <i>Забронируйте столик прямо сейчас, чтобы не упустить шанс насладиться уникальными блюдами!</i>\n\n" +
//                    "📲 Нажмите /menu, чтобы посмотреть всё меню!\n\n" +
//                    "❤️ С любовью, ваш ARNAUTS!", telegramUserEntity.getFirstname()));
//            try {
//                photo.setChatId(telegramUserEntity.getChatId().toString());
//                execute(photo);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("HTML");
        message.setText(textToSend);
        executeMessage(message);
    }

    private void handleCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();

        boolean anyMatchProductTypes = callbackProductTypesData.stream()
                .anyMatch(callbackItem -> callbackItem.equals(data));
        boolean anyMatchProducts = callbackProductsData.stream()
                .anyMatch(callbackItem -> callbackItem.equals(data));


        if (anyMatchProductTypes) {
            handleProductTypeCallback(callbackQuery, data);
        }
        if (anyMatchProducts) {
            setToProduct(update, data);

        } else if (data.equals(BUTTON_BACK_TO_MENU)) {
            backToMenu(update);
        }
    }

    public void setToProduct(Update update, String product) {
        ProductResponseDTO productResponse = productServiceImpl.getByName(product);
        StringBuilder productText = getProductText(productResponse);
        EditMessageText editMessage = setEditMessageTextProperties(update);
        editMessage.setParseMode("HTML");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        editMessage.setText(productText.toString());

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

        inlineKeyboardButton.setText("Назад ✨");
        inlineKeyboardButton.setCallbackData(productResponse.getTypeName());

        inlineKeyboardButtons.add(inlineKeyboardButton);
        rowsInLine.add(inlineKeyboardButtons);
        markupInLine.setKeyboard(rowsInLine);

        editMessage.setReplyMarkup(markupInLine);
        executeMessage(editMessage);
    }


    private StringBuilder getProductText(ProductResponseDTO productResponse) {
        StringBuilder productText = new StringBuilder();

        productText.append("🍴 <b>Блюдо:</b> ").append(productResponse.getName()).append("\n");
        productText.append("✨ <i>Описание:</i> ").append(productResponse.getDescription()).append("\n");
        productText.append("📂 <i>Категория:</i> ").append(productResponse.getTypeName()).append("\n");
        productText.append("💰 <b>Стоимость:</b> ").append(productResponse.getPrice()).append(" лей\n");
        LocalTime cookingTime = productResponse.getCookingTime();
        if (cookingTime != null) {
            productText.append("⏱️ <b>Время приготовления:</b> ").append(cookingTime.getMinute()).append(" минут\n");
        }

        productText.append("\n🍽️ Наслаждайтесь изысканным вкусом и уютной атмосферой! ❤️");
        return productText;
    }

    private EditMessageText setEditMessageTextProperties(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setText(menuText.toString());
        editMessage.setMessageId((int) messageId);
        editMessage.setParseMode("HTML");

        return editMessage;
    }


    private void backToMenu(Update update) {

        EditMessageText editMessage = setEditMessageTextProperties(update);
        InlineKeyboardMarkup menuInlineMarkup = getMenuInlineMarkup();
        editMessage.setText(menuText.toString());
        editMessage.setReplyMarkup(menuInlineMarkup);
        executeMessage(editMessage);
        deleteMenuText();
    }

    private void handleProductTypeCallback(CallbackQuery callbackQuery, String productType) {
        List<ProductResponseDTO> products = productService.getByTypeName(productType); // TODO: Implement representation of all products in Telegram buttons

        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();

        String responseText = "🍽️ Вы выбрали категорию <b>" + productType + "</b>!\n\n" +
                "Можете нажать на блюдо, чтобы увидеть подробное описание.\n\n"
                + "Вот, что мы с любовью приготовили для вас 😋:\n";

        editMessageProductsByType(responseText, chatId, messageId, products);
    }

    private void editMessageProductsByType(String text, long chatId, long messageId, List<ProductResponseDTO> products) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);
        message.setParseMode("HTML");


        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        int size = products.size();
        int rows = (int) Math.ceil((double) size / 2);


        // Разбиваем на строки по 3 кнопки
        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            // Индексы для кнопок в строке
            int limitation = Math.min((i + 2) * 2, size);
            for (int x = i * 3; x < limitation; x++) {
                InlineKeyboardButton button = createButton();
                String callbackData = products.get(x).getName();
                button.setText(callbackData);
                button.setCallbackData(callbackData);
                callbackProductsData.add(callbackData);
                row.add(button);
            }
            rowsInLine.add(row);
        }

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);


        addBackToMenuButton(rowsInLine);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void addBackToMenuButton(List<List<InlineKeyboardButton>> rowsInLine) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = createButton();
        button.setText("Назад ✨");
        button.setCallbackData(BUTTON_BACK_TO_MENU);
        row.add(button);
        rowsInLine.add(row);
    }


    private final StringBuilder menuText = new StringBuilder();

    private List<String> setMenuText() {
        List<String> productTypes = productTypeService.getAll().stream()
                .map(ProductTypeResponseDTO::getName).toList();

        menuText.append("🍽️ <i><b>Добро пожаловать в наше уютное меню!</b></i> \n\n")
                .append("✨ Здесь вы найдёте изысканные блюда, которые подарят вам наслаждение и радость! ✨\n\n");

        for (int i = 1; i <= productTypes.size(); i++) {
            menuText.append("\uD83D\uDD38 <b>")
                    .append(i).append(". ")
                    .append(productTypes.get(i - 1))
                    .append("</b> \n")
                    .append("   ───────────────\n"); // Разделитель между категориями
        }

        menuText.append("\n💌 Спасибо, что выбираете нас! Ваш вкус — наша забота! 💌\n")
                .append("🎉 <i>Введите номер категории или выберите из меню ниже!</i> 🎉\n");
        return productTypes;
    }

    private void menu(Update update) {
        setMenuText();
        SendMessage message = new SendMessage(update.getMessage().getChatId().toString(), menuText.toString());
        message.setParseMode("HTML");
        createMenu(message);
    }

    private void createMenu(SendMessage message) {
        InlineKeyboardMarkup menuInlineMarkup = getMenuInlineMarkup();
        message.setReplyMarkup(menuInlineMarkup);
        executeMessage(message);
        deleteMenuText();
    }

    //нужно после каждой отправки menu
    private void deleteMenuText() {
        menuText.delete(0, menuText.length());
    }

    private InlineKeyboardMarkup getMenuInlineMarkup() {

        List<String> productTypes = setMenuText();

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        int size = productTypes.size();
        int rows = (int) Math.ceil((double) size / 2); // Округление вверх для правильного расчета строк

        // Разбиваем на строки по 2 кнопки
        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            // Индексы для кнопок в строке
            int limitation = Math.min((i + 1) * 2, size);
            for (int x = i * 2; x < limitation; x++) {
                InlineKeyboardButton button = createButton();
                String callbackData = productTypes.get(x);
                button.setText("\uD83D\uDD38 " + callbackData);
                button.setCallbackData(callbackData);
                callbackProductTypesData.add(callbackData);
                row.add(button);
            }
            rowsInLine.add(row);
        }

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    private InlineKeyboardButton createButton() {
        return new InlineKeyboardButton();
    }


    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void executeMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


    private void stickerHandler(Update update) {
        String fileId = update.getMessage().getSticker().getFileId();
        sendMessage(update, "Какой прекрасный стикер! 🙃");
        log.info("Получен File ID стикера: {}", fileId);
    }

    private void sendHelpMessage(Update update) {

        sendMessage(update, helpText);
    }


    private void startRegister(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = update.getMessage().getFrom();
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(chatId, user);

        if (!telegramUserService.existByChatId(chatId)) {
            rabbitMQJsonProducer.send(userRegistrationDTO);
            sendSticker(chatId, "CAACAgIAAxkBAAOIZ2wCV5OzULOMka95E5_NGb48DX8AAocQAALddzlI382554aYWfM2BA");
            sendMessage(update, "Добро пожаловать в бот ресторана ARNAUT's! ☺ \n" +
                    "Введите /help, чтобы узнать, что я могу сделать.");
        } else {
            sendMessage(update, "Ой, вышла ошибочка 😅.\n" +
                    "Мы заметили, что вы уже запустили нашего бота 😽.\n" +
                    "Можете ввести /help, чтобы узнать, что я могу сделать. 😌");
        }
    }

    private void registerFull(Update update) {

        Long chatId = update.getMessage().getChatId();

        String errorText = String.format(
                "🌐Заходите на наш сайт https://arnauts.md/%d.\n" +
                        "🎁Участвуйте в розыгрышах, получайте промокоды и смотрите за новостями!", chatId);


        if (!telegramUserService.existByChatId(chatId)) {
            sendSticker(chatId, "CAACAgIAAxkBAAOMZ2wCg2GLi8plYN0NGFsVl2NfnMYAAgsBAAL3AsgPxfQ7mJWqcds2BA");
            try {
                telegramUserService.registerUser(update);

                String message = String.format("Поздравляем! Теперь вы являетесь частью нашей семьи!\n\n" +
                        "🌐Заходите на наш сайт https://arnauts.md/%d\n" +
                        "🎁Участвуйте в розыгрышах, получайте промокоды и смотрите за новостями!", chatId);

                sendMessageWithMarkdown(chatId, message);
            } catch (EntityNotFoundException e) {
                sendMessage(update, errorText);
            }
        } else {

            sendMessage(update, errorText);
        }
    }


    private void sendMessageWithMarkdown(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);
        sendMessage.setParseMode("Markdown"); // Использование Markdown для форматирования текста
        executeMessage(sendMessage);
    }


    private void sendSticker(Long chatId, String stickerFileId) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(chatId.toString());
        sendSticker.setSticker(new InputFile(stickerFileId)); // Используем File ID стикера или URL

        try {
            execute(sendSticker);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке стикера: {}", e.getMessage());
        }
    }


    private void sendMessage(Update update, String text) {
        SendMessage message = new SendMessage();
        message.setParseMode("HTML");
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("/menu");
        row.add("/website");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }

    }


}
