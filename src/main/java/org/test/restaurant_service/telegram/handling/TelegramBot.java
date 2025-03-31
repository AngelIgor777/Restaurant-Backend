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
import org.test.restaurant_service.dto.response.*;
import org.test.restaurant_service.entity.ProductTranslation;
import org.test.restaurant_service.entity.ProductTypeTranslation;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.mapper.ProductTypeTranslationMapper;
import org.test.restaurant_service.mapper.ProductTypeTranslationMapperImpl;
import org.test.restaurant_service.service.*;
import org.test.restaurant_service.service.impl.*;
import org.test.restaurant_service.telegram.config.BotConfig;
import org.test.restaurant_service.telegram.util.TextUtil;

import java.util.*;

@Slf4j
@Component
@EnableScheduling
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramUserService telegramUserService;
    private final ProductTypeService productTypeService;
    private final ProductService productService;
    private final TextUtil textUtil;
    private final BotConfig botConfig;
    private final UserService userService;
    private final S3Service s3Service;
    private final LanguageService languageService;
    private final ProductTranslationService productTranslationService;
    private final ProductTypeTranslationService productTypeTranslationService;
    private final ProductTypeTranslationMapper productTypeTranslationMapper;

    public TelegramBot(TelegramUserServiceImpl telegramUserService, ProductTypeServiceImpl productTypeService, @Qualifier("productServiceImpl") ProductServiceImpl productService, BotConfig botConfig, TextUtil textUtil, UserService userService, S3Service s3Service, LanguageService languageService, ProductTranslationService productTranslationService, ProductTypeTranslationService productTypeTranslationService, ProductTypeTranslationMapperImpl productTypeTranslationMapper) {
        this.telegramUserService = telegramUserService;
        this.productTypeService = productTypeService;
        this.productService = productService;
        this.botConfig = botConfig;
        this.textUtil = textUtil;
        this.userService = userService;
        this.s3Service = s3Service;
        this.languageService = languageService;
        this.productTranslationService = productTranslationService;
        this.productTypeTranslationService = productTypeTranslationService;
        ArrayList<BotCommand> botCommands = getCommands("ru");
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        this.productTypeTranslationMapper = productTypeTranslationMapper;
    }

    private Set<String> callbackProductTypesData = new HashSet<>();
    private Set<String> callbackProductsData = new HashSet<>();


    private ArrayList<BotCommand> getCommands(String langCode) {
        ArrayList<BotCommand> botCommands = new ArrayList<>();
        if ("ro".equals(langCode)) {
            botCommands.add(new BotCommand("/start", "Porniți botul"));
            botCommands.add(new BotCommand("/help", "Lista comenzilor disponibile"));
            botCommands.add(new BotCommand("/info", "Informații despre bot"));
            botCommands.add(new BotCommand("/menu", "Afișați meniul"));
            botCommands.add(new BotCommand("/about", "Afișați informațiile mele"));
            botCommands.add(new BotCommand("/lang", "Schimbați limba"));
        } else {
            botCommands.add(new BotCommand("/start", "Запуск бота"));
            botCommands.add(new BotCommand("/help", "Список доступных команд"));
            botCommands.add(new BotCommand("/info", "Информация о боте"));
            botCommands.add(new BotCommand("/menu", "Показать меню"));
            botCommands.add(new BotCommand("/about", "Показать мою информацию"));
            botCommands.add(new BotCommand("/lang", "Изменить язык"));
        }
        return botCommands;
    }

    private void updateBotCommands(String langCode) {
        try {
            this.execute(new SetMyCommands(getCommands(langCode), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Failed to update bot commands: {}", e.getMessage());
        }
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
        log.debug("Update: {}", update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            handleTextCommand(update, text);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        } else if (update.getMessage().hasSticker()) {
            stickerHandler(update);
        }
    }


    //todo add method which check the user select lang or not
    private void handleTextCommand(Update update, String text) {
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
                user = userService.findByChatId(chatId);
                sendMessage(update, textUtil.getInfoText(user.getTelegramUserEntity().getLanguage().getCode()));
                break;
            case "/menu":
                menu(update);
                break;
            case "/website":
                user = userService.findByChatId(chatId);
                sendMessageWithMarkdown(chatId, textUtil.getWebSiteText(user.getUuid(), user.getTelegramUserEntity().getLanguage().getCode()));
                break;
            case "/about":
                sendUserInfo(update);
                break;
            case "/lang":
                sendLanguageSelection(update.getMessage().getChatId());
                break;
            default:
                user = userService.findByChatId(chatId);
                sendMessageWithMarkdown(chatId, textUtil.getDefaultMessage(user.getUuid(), user.getTelegramUserEntity().getLanguage().getCode()));
                break;
        }
    }


    private void handleCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        log.debug("Recieved callbackQuery: {}", data);
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
        if (data.startsWith("LANG_")) {
            handleLanguageCallback(data, chatId);
        }
    }


    private void sendLanguageSelection(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Чтобы продолжить - выберите язык / Pentru a continua, selectați o limbă:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(List.of(createLangButton("Русский", "ru")));
        buttons.add(List.of(createLangButton("Română", "ro")));

        markup.setKeyboard(buttons);
        message.setReplyMarkup(markup);
        executeMessage(message);
    }

    private void sendUserInfo(Update update) {
        User user = userService.findByChatId(update.getMessage().getChatId());
        String userInfo = textUtil.getUserInfo(user);
        sendMessage(update, userInfo);
    }

    private String saveUserPhoto(Update update) {
        Long userId = update.getMessage().getFrom().getId();

        GetUserProfilePhotos getUserProfilePhotos = new GetUserProfilePhotos();
        getUserProfilePhotos.setUserId(userId);
        getUserProfilePhotos.setLimit(1);

        try {
            UserProfilePhotos photos = execute(getUserProfilePhotos);
            Integer totalCount = photos.getTotalCount();
            log.info("user photos count {}", totalCount);

            if (totalCount > 0) {
                log.info("User has photo profile");
                List<PhotoSize> photoSizes = photos.getPhotos().get(0);
                String fileId = photoSizes.get(photoSizes.size() - 1).getFileId();

                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                File file = execute(getFile);

                if (file != null && file.getFilePath() != null) {
                    String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();

                    String fileName = fileId + ".jpg";
                    String fileUrlInS3 = "https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697/uploads/images/" + fileName;

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

    @Transactional(rollbackFor = Exception.class)
    public void handleLanguageCallback(String data, Long chatId) {
        User user = userService.findByChatId(chatId);
        String langCode = data.substring(5);
        languageService.setLanguage(chatId, langCode);
        String confirmationMessage = "ro".equals(langCode) ? "Limba a fost setată ✅" : "Язык установлен ✅";
        sendMessageWithHTML(chatId, confirmationMessage);
        updateBotCommands(langCode);

        sendSticker(chatId, "CAACAgIAAxkBAAOMZ2wCg2GLi8plYN0NGFsVl2NfnMYAAgsBAAL3AsgPxfQ7mJWqcds2BA");
        String message = textUtil.getMessageAfterRegister(user.getUuid(), langCode);
        sendMessageWithMarkdown(chatId, message);
    }

    public void setToProduct(Update update, String productId) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        User user = userService.findByChatId(chatId);
        String langCode = user.getTelegramUserEntity().getLanguage().getCode();

        ProductResponseDTO productResponse = ProductMapper.INSTANCE.toResponseIgnorePhotos(productService.getSimpleById(Integer.parseInt(productId)));
        StringBuilder productText;
        ProductTypeTranslationResponseDTO productTypeTranslationResponseDTO = null;
        if (langCode.equals("ro")) {
            ProductTranslation productTranslation = productTranslationService.getTranslationByProductId(Integer.parseInt(productId));
            ProductTypeTranslation translation = productTypeTranslationService.getTranslation(productResponse.getTypeName(), "ro");
            productTypeTranslationResponseDTO = productTypeTranslationMapper.toTranslationDTO(translation);
            productText = textUtil.getProductTranslationRoText(productTranslation, productTypeTranslationResponseDTO);
        } else {
            productText = textUtil.getProductText(productResponse);
        }
        EditMessageText editMessage = setEditMessageTextProperties(update);
        editMessage.setParseMode("HTML");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        editMessage.setText(productText.toString());

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

        if (langCode.equals("ru")) {
            inlineKeyboardButton.setText("Назад ✨");
            inlineKeyboardButton.setCallbackData(productResponse.getTypeName());
        } else {
            inlineKeyboardButton.setText("Înapoi ✨");
            inlineKeyboardButton.setCallbackData(productTypeTranslationResponseDTO.getName());
        }

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
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        User user = userService.findByChatId(chatId);

        EditMessageText editMessage = setEditMessageTextProperties(update);
        InlineKeyboardMarkup menuInlineMarkup = getMenuInlineMarkup(user.getTelegramUserEntity().getLanguage().getCode());
        editMessage.setText(menuText.toString());
        editMessage.setReplyMarkup(menuInlineMarkup);
        executeMessage(editMessage);
        deleteMenuText();
    }

    private void handleProductTypeCallback(CallbackQuery callbackQuery, String productType) {
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();
        User user = userService.findByChatId(chatId);
        String langCode = user.getTelegramUserEntity().getLanguage().getCode();

        String responseText = textUtil.getProductTypeTextByType(productType, user.getTelegramUserEntity().getLanguage().getCode());


        if (langCode.equals("ro")) {
            String productNameRu = productTypeTranslationService.getByRoTranslation(productType, "ro").getProductType().getName();

            List<ProductResponseDTO> products = productService.getByTypeName(productNameRu);

            List<ProductTelegramResponseDto> list = products
                    .stream()
                    .map(productResponseDTO -> {
                        ProductTranslationResponseDTO productTranslationResponseDTO = productTranslationService.getTranslation(productResponseDTO.getId(), "ro");
                        ProductTelegramResponseDto productTelegramResponseDto = new ProductTelegramResponseDto();
                        productTelegramResponseDto.setProductId(productTranslationResponseDTO.getProductId());
                        productTelegramResponseDto.setProductName(productTranslationResponseDTO.getName());
                        return productTelegramResponseDto;
                    })
                    .toList();

            editMessageProductsByType(responseText, chatId, messageId, list, langCode);

        } else {
            List<ProductResponseDTO> products = productService.getByTypeName(productType);
            List<ProductTelegramResponseDto> strings = products
                    .stream()
                    .map(productResponseDTO -> {
                        ProductTelegramResponseDto productTelegramResponseDto = new ProductTelegramResponseDto();
                        productTelegramResponseDto.setProductId(productResponseDTO.getId());
                        productTelegramResponseDto.setProductName(productResponseDTO.getName());
                        return productTelegramResponseDto;
                    })
                    .toList();
            editMessageProductsByType(responseText, chatId, messageId, strings, langCode);
        }

    }

    private void editMessageProductsByType(String text, long chatId, long messageId, List<ProductTelegramResponseDto> productTelegramResponseDtoList, String langCode) {
        EditMessageText message = getEditMessageText(String.valueOf(chatId), text, (int) messageId);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = createProductButtons(productTelegramResponseDtoList);

        addBackToMenuButton(rowsInLine, langCode);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        sendEditedMessage(message);
    }

    private List<List<InlineKeyboardButton>> createProductButtons(List<ProductTelegramResponseDto> productTelegramResponseDtoList) {
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        int size = productTelegramResponseDtoList.size();
        int buttonsPerRow = 3;

        callbackProductsData.clear();

        for (int i = 0; i < size; i += buttonsPerRow) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < Math.min(i + buttonsPerRow, size); j++) {
                InlineKeyboardButton button = createButton();
                ProductTelegramResponseDto productTelegramResponseDto = productTelegramResponseDtoList.get(j);
                String productName = productTelegramResponseDto.getProductName();
                button.setText(productName);
                String callbackData = String.valueOf(productTelegramResponseDto.getProductId());
                button.setCallbackData(callbackData);
                callbackProductsData.add(callbackData);
                row.add(button);

                log.debug("Add button '{}' with callbackData '{}'", productName, callbackData);
            }
            rowsInLine.add(row);
        }
        log.debug("callbackProductsData content: {}", callbackProductsData);
        return rowsInLine;
    }

    private void sendEditedMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending edited message: {}", e.getMessage());
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

    private void addBackToMenuButton(List<List<InlineKeyboardButton>> rowsInLine, String langCode) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = createButton();
        if (langCode.equals("ru")) {
            button.setText("Назад ✨");
        } else {
            button.setText("Înapoi ✨");
        }
        button.setCallbackData(CallBackButton.BACK_TO_MENU.toString());
        row.add(button);
        rowsInLine.add(row);
    }


    private final StringBuilder menuText = new StringBuilder();

    private void menu(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userService.findByChatId(chatId);

        String langCode = user.getTelegramUserEntity().getLanguage().getCode();
        setMenuText(langCode);
        SendMessage message = new SendMessage(update.getMessage().getChatId().toString(), menuText.toString());
        message.setParseMode("HTML");
        createMenu(message, langCode);
    }

    private List<String> setMenuText(String langCode) {
        List<ProductTypeResponseDTO> all = productTypeService.getAll();
        List<String> productTypes = new ArrayList<>();

        if (langCode.equals("ro")) {
            for (ProductTypeResponseDTO productTypeResponseDTO : all) {
                ProductTypeTranslationResponseDTO ro = productTypeTranslationService.getTranslation(productTypeResponseDTO.getId(), "ro");
                productTypes.add(ro.getName());
            }
        } else {
            all.stream()
                    .forEach(productTypeResponseDTO -> {
                        String name = productTypeResponseDTO.getName();
                        productTypes.add(name);
                    });
        }

        textUtil.addAllProductsToMenu(menuText, productTypes, langCode);
        return productTypes;
    }

    private void createMenu(SendMessage message, String langCode) {
        InlineKeyboardMarkup menuInlineMarkup = getMenuInlineMarkup(langCode);
        message.setReplyMarkup(menuInlineMarkup);
        executeMessage(message);
        deleteMenuText();
    }

    //нужно после каждой отправки menu
    private void deleteMenuText() {
        menuText.delete(0, menuText.length());
    }

    private InlineKeyboardMarkup getMenuInlineMarkup(String langCode) {

        List<String> productTypes = setMenuText(langCode);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        int size = productTypes.size();
        int rows = (int) Math.ceil((double) size / 2);

        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            int limitation = Math.min((i + 1) * 2, size);

            for (int x = i * 2; x < limitation; x++) {
                InlineKeyboardButton button = createButton();
                String callbackData = productTypes.get(x);
                button.setText("\uD83D\uDD38 " + callbackData);
                button.setCallbackData(callbackData);
                callbackProductTypesData.add(callbackData);
                row.add(button);
                log.debug("Add button: {} with callbackData: {}", callbackData, callbackData);
                log.debug("callbackProductTypesData data: {}", callbackProductTypesData);
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

    private void sendHelpMessage(Long chatId) {
        User user = userService.findByChatId(chatId);
        String codeLang = user.getTelegramUserEntity().getLanguage().getCode();
        sendMessage(chatId, textUtil.getHelpText(codeLang));
    }

    private void sendHelpMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userService.findByChatId(chatId);
        String codeLang = user.getTelegramUserEntity().getLanguage().getCode();
        sendMessage(chatId, textUtil.getHelpText(codeLang));
    }

    @Transactional(rollbackFor = Exception.class)
    public void registerFull(Update update) {
        Long chatId = update.getMessage().getChatId();
        String errorText;
        if (!telegramUserService.existByChatId(chatId)) {
            String userPhotoUrl = saveUserPhoto(update);
            telegramUserService.registerUser(update, userPhotoUrl);
            sendLanguageSelection(update.getMessage().getChatId());
        } else {
            User user = userService.findByChatId(chatId);
            UUID userUUID = user.getUuid();
            errorText = textUtil.getErrorText(userUUID, user.getTelegramUserEntity().getLanguage().getCode());
            sendMessageWithMarkdown(chatId, errorText);
        }

    }

    private InlineKeyboardButton createLangButton(String text, String code) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData("LANG_" + code);
        return button;
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

    public void sendMessageWithHTML(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);
        ReplyKeyboardMarkup replyKeyboard = getReplyKeyboard();
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setParseMode("HTML"); // Использование Markdown для форматирования текста
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

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setParseMode("HTML");
        message.setChatId(chatId.toString());
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
