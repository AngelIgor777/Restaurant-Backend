package org.test.restaurant_service.telegram.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.rabbitmq.producer.RabbitMQJsonProducer;
import org.test.restaurant_service.service.*;
import org.test.restaurant_service.service.impl.*;
import org.test.restaurant_service.telegram.config.BotConfig;
import org.test.restaurant_service.telegram.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@EnableScheduling
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramUserService telegramUserService;
    private final ProductTypeService productTypeService;
    private final ProductService productService;
    private final RabbitMQJsonProducer rabbitMQJsonProducer;
    private final TextUtil textUtil;
    private final BotConfig botConfig;
    private final PhotoServiceImpl photoService;
    private final UserService userService;
    private final S3Service s3Service;

    public TelegramBot(TelegramUserServiceImpl telegramUserService, ProductTypeServiceImpl productTypeService, @Qualifier("productServiceImpl") ProductServiceImpl productService, RabbitMQJsonProducer rabbitMQJsonProducer, BotConfig botConfig, PhotoServiceImpl photoServiceImpl, TextUtil textUtil, UserServiceImpl userServiceImpl,@Qualifier("photoServiceImplS3") PhotoServiceImpl photoService, UserService userService, S3Service s3Service) {
        this.telegramUserService = telegramUserService;
        this.productTypeService = productTypeService;
        this.productService = productService;
        this.rabbitMQJsonProducer = rabbitMQJsonProducer;

        this.botConfig = botConfig;
        this.textUtil = textUtil;
        this.photoService = photoService;
        this.userService = userService;
        this.s3Service = s3Service;
        ArrayList<BotCommand> botCommands = getCommands();
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private List<String> callbackProductTypesData = new CopyOnWriteArrayList<>();
    private List<String> callbackProductsData = new CopyOnWriteArrayList<>();


    private static ArrayList<BotCommand> getCommands() {
        ArrayList<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "Запуск бота"));
        botCommands.add(new BotCommand("/help", "Список доступных команд"));
        botCommands.add(new BotCommand("/info", "Информация о боте"));
        botCommands.add(new BotCommand("/menu", "Показать меню"));
        botCommands.add(new BotCommand("/about", "Показать мою информацию"));
        return botCommands;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotKey();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            Long chatId = update.getMessage().getChatId();
            User user;
            switch (text) {
                case "/start":
                    registerFull(update);
                    break;
                case "/help":
                    sendHelpMessage(update);
                    break;
                case "/info":
                    sendMessage(update, textUtil.getInfoText());
                    break;
                case "/menu":
                    menu(update);
                    break;
                case "/website":
                    user = userService.findByChatId(chatId);
                    sendMessageWithMarkdown(chatId, textUtil.getWebSiteText(user.getUuid()));
                    break;
                case "/about":
                    sendUserInfo(update);
                    break;
                default:
                    user = userService.findByChatId(chatId);
                    sendMessageWithMarkdown(chatId, textUtil.getDefaultMessage(user.getUuid()));
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);

        } else if (update.getMessage().hasSticker()) {
            stickerHandler(update);
        }
    }

    private void sendUserInfo(Update update) {
        User user = userService.findByChatId(update.getMessage().getChatId());
        String userInfo = textUtil.getUserInfo(user);
        sendMessage(update, userInfo);
    }

    private String saveUserPhoto(Update update) {
        Long userId = update.getMessage().getFrom().getId();

        // Request user profile photos
        GetUserProfilePhotos getUserProfilePhotos = new GetUserProfilePhotos();
        getUserProfilePhotos.setUserId(userId);
        getUserProfilePhotos.setLimit(1); // Get only the latest photo

        try {
            UserProfilePhotos photos = execute(getUserProfilePhotos);
            Integer totalCount = photos.getTotalCount();
            log.info("user photos count {}", totalCount);

            if (totalCount > 0) {
                log.info("User has photo profile");
                List<PhotoSize> photoSizes = photos.getPhotos().get(0); // Get the first set of photos
                String fileId = photoSizes.get(photoSizes.size() - 1).getFileId(); // Get the highest resolution

                // Get file path from Telegram servers
                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);

                if (file != null && file.getFilePath() != null) {
                    // Construct the file download URL
                    String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();

                    String fileName = fileId + ".jpg";
                    String fileUrlInS3 = "https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697/uploads/images/" + fileName;

                    // Download and save the image
                    s3Service.upload(fileUrl, fileName);
                    return fileUrlInS3;
                } else {
                    log.warn("Could not retrieve file path.");
                }
            } else {
                log.info("User has no profile photo.");
            }
        } catch (TelegramApiException e) {
            log.warn(e.getMessage());
        }
        return null;
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

        } else if (data.equals(CallBackButton.BACK_TO_MENU.toString())) {
            backToMenu(update);
        }
    }

    public void setToProduct(Update update, String product) {
        ProductResponseDTO productResponse = productService.getByName(product);
        StringBuilder productText = textUtil.getProductText(productResponse);
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


    private EditMessageText setEditMessageTextProperties(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EditMessageText editMessage = getEditMessageText(String.valueOf(chatId), menuText.toString(), (int) messageId);

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
        List<ProductResponseDTO> products = productService.getByTypeName(productType);

        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();

        String responseText = textUtil.getProductTypeTextByType(productType);

        editMessageProductsByType(responseText, chatId, messageId, products);
    }

    private void editMessageProductsByType(String text, long chatId, long messageId, List<ProductResponseDTO> products) {
        EditMessageText message = getEditMessageText(String.valueOf(chatId), text, (int) messageId);
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

    private static EditMessageText getEditMessageText(String chatId, String text, int messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId(messageId);
        message.setParseMode("HTML");
        return message;
    }

    private void addBackToMenuButton(List<List<InlineKeyboardButton>> rowsInLine) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = createButton();
        button.setText("Назад ✨");
        button.setCallbackData(CallBackButton.BACK_TO_MENU.toString());
        row.add(button);
        rowsInLine.add(row);
    }


    private final StringBuilder menuText = new StringBuilder();

    private List<String> setMenuText() {
        List<String> productTypes = productTypeService.getAll().stream()
                .map(ProductTypeResponseDTO::getName).toList();

        textUtil.addAllProductsToMenu(menuText, productTypes);
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

        sendMessage(update, textUtil.getHelpText());
    }

    @Transactional(rollbackFor = Exception.class)
    public void registerFull(Update update) {

        Long chatId = update.getMessage().getChatId();

        String errorText;


        if (!telegramUserService.existByChatId(chatId)) {
            String userPhotoUrl = saveUserPhoto(update);
            User createdUser = telegramUserService.registerUser(update, userPhotoUrl);
            String message = textUtil.getMessageAfterRegister(createdUser.getUuid());


            sendSticker(chatId, "CAACAgIAAxkBAAOMZ2wCg2GLi8plYN0NGFsVl2NfnMYAAgsBAAL3AsgPxfQ7mJWqcds2BA");
            sendMessageWithMarkdown(chatId, message);
        } else {
            UUID userUUID = userService.findByChatId(chatId).getUuid();
            errorText = textUtil.getErrorText(userUUID);
            sendMessageWithMarkdown(chatId, errorText);
        }
    }


    public void sendMessageWithMarkdown(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);
        ReplyKeyboardMarkup replyKeyboard = getReplyKeyboard();
        sendMessage.setReplyMarkup(replyKeyboard);
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


    public void sendMessage(Update update, String text) {
        SendMessage message = new SendMessage();
        message.setParseMode("HTML");
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = getReplyKeyboard();

        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }

    }

    private static ReplyKeyboardMarkup getReplyKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("/menu");
        row.add("/website");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private enum CallBackButton {
        BACK_TO_MENU;
    }

}
