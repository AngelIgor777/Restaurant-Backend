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
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestWithPayloadDto;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.dto.response.*;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.mapper.ProductTypeTranslationMapper;
import org.test.restaurant_service.mapper.ProductTypeTranslationMapperImpl;
import org.test.restaurant_service.rabbitmq.producer.RabbitMQJsonProducer;
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
    private final TableService tableService;
    private final RabbitMQJsonProducer rabbitMQJsonProducer;
    private final UserCacheService userCacheService;

    private final String QUICK_ORDER_SUFFIX = "QO:";
    private final String LANG_SUFFIX = "LANG_";

    //order types
    private final String ORDER_WITH_YOURSELF = "OT:Y";
    private final String ORDER_TO_TABLE = "OT:T";
    private final String ORDER_HOME = "OT:H";


    private final String TABLE_SUFFIX = "T:";

    private final String PRODUCT_TYPE_SUFFIX = "PT:";
    private final String PRODUCT_TYPE_WHEN_PRODUCT_SUFFIX = "WP:";

    private final String PAYMENT_CARD = "PCD";
    private final String PAYMENT_CASH = "PCH";

    private final String USER_WAITING_STATE_ADDRESS = "WT_ADDR";
    private final String USER_WAITING_STATE_PHONE = "WT_PH";
    private final OrderCacheService orderCacheService;

    public TelegramBot(TelegramUserServiceImpl telegramUserService, ProductTypeServiceImpl productTypeService, @Qualifier("productServiceImpl") ProductServiceImpl productService, BotConfig botConfig, TextUtil textUtil, UserService userService, S3Service s3Service, LanguageService languageService, ProductTranslationService productTranslationService, ProductTypeTranslationService productTypeTranslationService, ProductTypeTranslationMapperImpl productTypeTranslationMapper, TableService tableService, RabbitMQJsonProducer rabbitMQJsonProducer, UserCacheService userCacheService, OrderCacheService orderCacheService) {
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
        this.tableService = tableService;
        this.rabbitMQJsonProducer = rabbitMQJsonProducer;
        this.userCacheService = userCacheService;
        ArrayList<BotCommand> botCommands = getCommands("ru");
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        this.productTypeTranslationMapper = productTypeTranslationMapper;
        this.orderCacheService = orderCacheService;
    }

    private Set<String> callbackProductTypesData = new HashSet<>();

    private Set<String> callbackProductTypesDataWithDeleting = new HashSet<>();


    private Set<String> callbackProductsData = new HashSet<>();

    private ArrayList<BotCommand> getCommands(String langCode) {
        ArrayList<BotCommand> botCommands = new ArrayList<>();
        if ("ro".equals(langCode)) {
            botCommands.add(new BotCommand("/start", "Porniți botul"));
            botCommands.add(new BotCommand("/website", "Accesați site-ul web"));
            botCommands.add(new BotCommand("/help", "Lista comenzilor disponibile"));
            botCommands.add(new BotCommand("/info", "Informații despre bot"));
            botCommands.add(new BotCommand("/menu", "Afișați meniul"));
            botCommands.add(new BotCommand("/about", "Afișați informațiile mele"));
            botCommands.add(new BotCommand("/lang", "Schimbați limba"));
        } else {
            botCommands.add(new BotCommand("/start", "Запуск бота"));
            botCommands.add(new BotCommand("/website", "Зайти на сайт"));
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

    private void handleTextCommand(Update update, String text) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        User user;
        String userState = userCacheService.getUserState(chatId);
        if (userState != null) {
            if (userState.equals(USER_WAITING_STATE_ADDRESS)) {
                handleUserAddressMessage(update);
            } else if (userState.equals(USER_WAITING_STATE_PHONE)) {
                handlePhoneMessage(update);
            }
        } else {
            switch (text) {
                case "/menu":
                    menu(update);
                    break;
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
                case "/website":
                    user = userService.findByChatId(chatId);
                    sendMessageWithMarkdown(chatId, textUtil.getWebSiteText(user.getUuid(), user.getTelegramUserEntity().getLanguage().getCode()));
                    break;
                case "/about":
                    sendUserInfo(update);
                    break;
                case "/lang":
                    sendLanguageSelection(message.getChatId());
                    break;
                default:
                    user = userService.findByChatId(chatId);
                    sendMessageWithMarkdown(chatId, textUtil.getDefaultMessage(user.getUuid(), user.getTelegramUserEntity().getLanguage().getCode()));
                    break;
            }
        }
    }

    private void handleCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        log.debug("Recieved callbackQuery: {}", data);

        boolean langCallback = data.startsWith(LANG_SUFFIX);
        boolean quickOrderCallback = data.startsWith(QUICK_ORDER_SUFFIX);
        boolean quickOrderTypeCallback = data.startsWith("O"); //orders type always start with "O"
        boolean tableCallback = data.startsWith(TABLE_SUFFIX);
        boolean paymentMethodCallBack = data.startsWith("P"); // payment method start with "P"
        boolean anyMatchProductTypes = callbackProductTypesData.stream()
                .anyMatch(callbackItem -> callbackItem.equals(data));

        boolean anyMatchProductTypesWithDeleting = callbackProductTypesDataWithDeleting.stream()
                .anyMatch(callbackItem -> callbackItem.equals(data));
        boolean anyMatchProducts = callbackProductsData.stream()
                .anyMatch(callbackItem -> callbackItem.equals(data));
        if (anyMatchProductTypes) {
            handleProductTypeCallback(callbackQuery, data);
        } else if (anyMatchProducts) {
            setToProduct(update, data);
        } else if (anyMatchProductTypesWithDeleting) {
            handleProductTypeCallbackWithSendingNewMessage(callbackQuery, data);//
        } else if (quickOrderCallback) {
            handleQuickOrderCallback(update);
        } else if (quickOrderTypeCallback) {
            handleQuickOrderTypeCallback(update);
        } else if (tableCallback) {
            handleTableCallback(update);
        } else if (data.equals(CallBackButton.BACK_TO_MENU.toString())) {
            backToMenu(update);
        } else if (paymentMethodCallBack) {
            handlePaymentMethodCallBack(update);
        } else if (langCallback) {
            handleLanguageCallback(data, chatId);
        }
    }

    private void handlePaymentMethodCallBack(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        OrderProductRequestWithPayloadDto order = orderCacheService.getOrder(chatId);
        if (data.startsWith(PAYMENT_CARD)) {
            order.setPaymentMethod(Order.PaymentMethod.CARD);
        } else if (data.startsWith(PAYMENT_CASH)) {
            order.setPaymentMethod(Order.PaymentMethod.CASH);
        }
        OtpResponseDto otpResponseDto = rabbitMQJsonProducer.send(order);
        orderCacheService.deleteOrder(chatId);
        String string = "Номер вашего заказа: " + otpResponseDto.getOtp() +
                "\n\n\nВ скором времени всё будет готово! 😉\nА пока можете зайти на наш сайт и посмотреть меню более детально [parktown.md](http://195.133.27.38/#menu/%s)";

        EditMessageText editMessageText = getEditMessageText(update, string);

        sendMessageWithMarkdown(editMessageText);
    }

    private void handleUserAddressMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        User user = userService.findByChatId(chatId);
        String text = message.getText();
        OrderProductRequestWithPayloadDto order = orderCacheService.getOrder(chatId);
        order.setOrderInRestaurant(false);
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO();
        addressRequestDTO.setUserUUID(user.getUuid());
        addressRequestDTO.setStreet(text);
        addressRequestDTO.setUserUUID(user.getUuid());
        order.setAddressRequestDTO(addressRequestDTO);
        orderCacheService.saveOrder(chatId, order);

        sendPhone(chatId);
    }


    private void sendPaymentMethod(Long chatId) {
        String paymentMethodText = "Выберите метод оплаты";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(paymentMethodText);

        InlineKeyboardMarkup markup = getInlineKeyboardMarkupForPayment();
        message.setReplyMarkup(markup);
        executeMessage(message);
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkupForPayment() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(List.of(
                createOneLineButton("Карта", PAYMENT_CARD),
                createOneLineButton("Наличные", PAYMENT_CASH)));
        markup.setKeyboard(buttons);
        return markup;
    }

    private void sendPhone(Long chatId) {
        String text = "Введите номер вашего мобильного телефона:";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        userCacheService.saveUserState(chatId, USER_WAITING_STATE_PHONE);
        executeMessage(message);
    }


    private void handlePhoneMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();
        OrderProductRequestWithPayloadDto order = orderCacheService.getOrder(chatId);
        order.setPhoneNumber(text);
        orderCacheService.saveOrder(chatId, order);

        userCacheService.removeUserState(chatId);
        sendPaymentMethod(chatId);
    }


    private void handleTableCallback(Update update) {
        String paymentMethodText = "Выберите метод оплаты";
        EditMessageText editMessageText = getEditMessageText(update, paymentMethodText);

        InlineKeyboardMarkup markup = getInlineKeyboardMarkupForPayment();
        editMessageText.setReplyMarkup(markup);
        executeMessage(editMessageText);

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String tableNumber = callbackQuery.getData().substring(TABLE_SUFFIX.length());
        Long chatId = callbackQuery.getMessage().getChatId();
        OrderProductRequestWithPayloadDto order = orderCacheService.getOrder(chatId);
        order.setOrderInRestaurant(true);
        order.setTableRequestDTO(new TableRequestDTO(Integer.parseInt(tableNumber)));
        orderCacheService.saveOrder(chatId, order);
    }

    private void handleQuickOrderTypeCallback(Update update) {
        EditMessageText editMessageText = getEditMessageText(update);
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        String data = callbackQuery.getData();
        if (data.equals(ORDER_TO_TABLE)) {
            editMessageText.setText("Выберите столик:");

            byte size = tableService.countAll();
            int buttonsPerRow = 4;
            int rows = (int) Math.ceil((double) size / buttonsPerRow);
            //4 tables per row
            for (int i = 0; i < rows; i++) {
                List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
                for (int j = i * buttonsPerRow + 1; j <= Math.min(size, (i + 1) * buttonsPerRow); j++) {
                    String table = String.valueOf(j);
                    InlineKeyboardButton tempOneLineButton
                            = createOneLineButton(table, TABLE_SUFFIX + table);
                    inlineKeyboardButtons.add(tempOneLineButton);
                }
                buttons.add(inlineKeyboardButtons);
            }
        } else if (data.equals(ORDER_HOME)) {
            editMessageText.setText("Отправьте мне в одну строку ваш адрес.\nНапишите город/село, улицу, дом");
            userCacheService.saveUserState(chatId, USER_WAITING_STATE_ADDRESS); // Store in Redis
        }
        markup.setKeyboard(buttons);
        editMessageText.setReplyMarkup(markup);
        executeMessage(editMessageText);
    }

    private EditMessageText getEditMessageText(Update update, String text) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        return getEditMessageText(String.valueOf(chatId), text, messageId);
    }

    private EditMessageText getEditMessageText(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        return getEditMessageText(String.valueOf(chatId), messageId);
    }

    private void handleQuickOrderCallback(Update update) {
        OrderProductRequestWithPayloadDto orderDto = new OrderProductRequestWithPayloadDto();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        String productId = data.substring(QUICK_ORDER_SUFFIX.length());
        Message message = callbackQuery.getMessage();
        Long chatId = message.getChatId();
        UUID userUUID = userService.findByChatId(chatId).getUuid();


        String text = "Выберите тип заказа";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");

        OrderProductRequestDTO orderProductRequestDTO = new OrderProductRequestDTO();
        orderProductRequestDTO.setProductId(Integer.valueOf(productId));
        orderProductRequestDTO.setQuantity(1);
        orderDto.setOrderProductRequestDTO(List.of(orderProductRequestDTO));
        orderDto.setUserRegistered(true);
        orderDto.setUserUUID(userUUID);
        orderCacheService.saveOrder(chatId, orderDto);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(List.of(createOneLineButton("На столик 🍽", ORDER_TO_TABLE)));
        buttons.add(List.of(createOneLineButton("Домой 🏠", ORDER_HOME)));

        markup.setKeyboard(buttons);
        sendMessage.setReplyMarkup(markup);

        executeMessage(sendMessage);
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

        ProductResponseDTO productResponse = ProductMapper.INSTANCE.toResponseDTO(productService.getSimpleById(Integer.parseInt(productId)));
        String photoUrl = productResponse.getPhotoUrl();
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

        // Creating a SendPhoto message instead of EditMessageText
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(photoUrl));  // Use InputFile to send photo by URL
        sendPhoto.setCaption(productText.toString());
        sendPhoto.setParseMode("HTML");

        // Setting inline keyboard
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons2 = new ArrayList<>();

        InlineKeyboardButton inlineBackKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineQuickOrderButton = new InlineKeyboardButton();
        addButtons(langCode, productResponse, productTypeTranslationResponseDTO, inlineBackKeyboardButton, inlineQuickOrderButton);

        inlineKeyboardButtons.add(inlineBackKeyboardButton);
        rowsInLine.add(inlineKeyboardButtons);
        markupInLine.setKeyboard(rowsInLine);

        inlineKeyboardButtons2.add(inlineQuickOrderButton);
        rowsInLine.add(inlineKeyboardButtons2);

        sendPhoto.setReplyMarkup(markupInLine);

        executeMessage(sendPhoto);
    }

    private void addButtons(String langCode,
                            ProductResponseDTO productResponse,
                            ProductTypeTranslationResponseDTO productTypeTranslationResponseDTO,
                            InlineKeyboardButton backToTypesButton,
                            InlineKeyboardButton quickOrderButton) {
        if (langCode.equals("ru")) {
            backToTypesButton.setText("Назад ✨");
            String productTypeCallbackData = PRODUCT_TYPE_WHEN_PRODUCT_SUFFIX + productResponse.getTypeName();
            backToTypesButton.setCallbackData(productTypeCallbackData);
            callbackProductTypesDataWithDeleting.add(productTypeCallbackData);
            quickOrderButton.setText("Быстрый заказ 🔔");
            quickOrderButton.setCallbackData(QUICK_ORDER_SUFFIX + productResponse.getId().toString());

        } else {
            backToTypesButton.setText("Înapoi ✨");
            backToTypesButton.setCallbackData(productTypeTranslationResponseDTO.getName());
        }
    }


    private EditMessageText setEditMessageTextProperties(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        return getEditMessageText(String.valueOf(chatId), menuText.toString(), messageId);
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

    private void handleProductTypeCallback(CallbackQuery callbackQuery, String data) {
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();
        User user = userService.findByChatId(chatId);
        String langCode = user.getTelegramUserEntity().getLanguage().getCode();

        String productType = data.substring(PRODUCT_TYPE_SUFFIX.length());

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

    private void handleProductTypeCallbackWithSendingNewMessage(CallbackQuery callbackQuery, String data) {
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();
        User user = userService.findByChatId(chatId);
        String langCode = user.getTelegramUserEntity().getLanguage().getCode();

        String productType = data.substring(PRODUCT_TYPE_SUFFIX.length());

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

            editMessageProductsByTypeWithSendingNewMessage(responseText, chatId, messageId, list, langCode);

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
            editMessageProductsByTypeWithSendingNewMessage(responseText, chatId, messageId, strings, langCode);
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

    private void editMessageProductsByTypeWithSendingNewMessage(String text, long chatId, long messageId, List<
            ProductTelegramResponseDto> productTelegramResponseDtoList, String langCode) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = createProductButtons(productTelegramResponseDtoList);

        addBackToMenuButton(rowsInLine, langCode);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        sendMessageWithHTML(message);
    }

    private List<List<InlineKeyboardButton>> createProductButtons
            (List<ProductTelegramResponseDto> productTelegramResponseDtoList) {
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        int size = productTelegramResponseDtoList.size();
        int buttonsPerRow = 3;

        callbackProductsData.clear();

        for (int i = 0; i < size; i += buttonsPerRow) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < Math.min(i + buttonsPerRow, size); j++) {
                InlineKeyboardButton button = createOneLineButton();
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

    private static EditMessageText getEditMessageText(String chatId, int messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setParseMode("HTML");
        return message;
    }

    private void addBackToMenuButton(List<List<InlineKeyboardButton>> rowsInLine, String langCode) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = createOneLineButton();
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
                InlineKeyboardButton button = createOneLineButton();
                String callbackData = productTypes.get(x);
                button.setText("\uD83D\uDD38 " + callbackData);
                button.setCallbackData(PRODUCT_TYPE_SUFFIX + callbackData);
                callbackProductTypesData.add(PRODUCT_TYPE_SUFFIX + callbackData);
                row.add(button);
                log.debug("Add button: {} with callbackData: {}", callbackData, callbackData);
                log.debug("callbackProductTypesData data: {}", callbackProductTypesData);
            }
            rowsInLine.add(row);
        }

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    private InlineKeyboardButton createOneLineButton() {
        return new InlineKeyboardButton();
    }


    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void executeMessage(DeleteMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void executeMessage(SendPhoto message) {
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
        button.setCallbackData(LANG_SUFFIX + code);
        return button;
    }

    private InlineKeyboardButton createOneLineButton(String text, String callBack) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBack);
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

    public void sendMessageWithMarkdown(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboard = getReplyKeyboard();
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setParseMode("Markdown");
        executeMessage(sendMessage);
    }

    public void sendMessageWithMarkdown(EditMessageText sendMessage) {
        sendMessage.setParseMode("Markdown");
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

    public void sendMessageWithHTML(SendMessage sendMessage) {
        sendMessage.setParseMode("HTML");
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

    private ReplyKeyboardMarkup getReplyKeyboard() {
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
