package org.test.restaurant_service.telegram.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.service.OrderService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Getter
public
class TextUtil {

    private final OrderService orderService;

    private String adText =
            """
                    🍽️ <b>Время вкусных открытий!</b>
                    
                    ✨ Сегодня у нас для вас нечто особенное:
                    🍕 <b>Пицца недели:</b> Сырный взрыв — только 149 mdl!
                    🍹 <b>Коктейли:</b> Закажи два и получи третий в подарок!
                    
                    🎉 <i>Забронируйте столик прямо сейчас, чтобы не упустить шанс насладиться уникальными блюдами!</i>
                    
                    📲 Нажмите /menu, чтобы посмотреть всё меню!
                    
                    ❤️ С любовью, ваш PARK TOWN""";

    private final String helpText =
            """
                    📖 <b>Доступные команды:</b>
                    
                    🚀 /start - <i>Запуск бота</i>
                    ❓ /help - <i>Список доступных команд</i>
                    ℹ️ /info - <i>Информация о боте</i>
                    🍽️ /menu - <i>Показать меню</i>
                    
                    ✨ Используйте команды, чтобы сделать ваше настроение более радостным!""";
    private final String infoText =
            """
                    🤖 <b>Добро пожаловать!</b>
                    
                    Этот бот создан, чтобы сделать вашу жизнь проще и приятнее! 🌟
                    
                    С его помощью вы можете:
                    📝 Заказать еду на нашем сайте
                    📢 Получать самые свежие новости о мероприятиях PARK TOWN
                    
                    ✨ Мы всегда рады быть вам полезными!""";

    public StringBuilder getProductText(ProductResponseDTO productResponse) {
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

    public String getCaptionForUser(TelegramUserEntity telegramUserEntity) {
        return String.format("""
                %s Привет! 😎 У нас для тебя что-то новенькое!
                
                🍽️<b>Время вкусных открытий!</b>
                
                ✨ Сегодня у нас для вас нечто особенное:
                🍕 <b>Пицца недели:</b> Сырный взрыв — только 149 лей!
                🎉 <i>Забронируйте столик прямо сейчас, чтобы не упустить шанс насладиться уникальными блюдами!</i>
                
                📲 Нажмите /menu, чтобы посмотреть всё меню или переходи !
                
                ❤️ С любовью, ваш PARK TOWN""", telegramUserEntity.getFirstname());
    }

    public String getProductTypeTextByType(String productType) {
        return "🍽️ Вы выбрали категорию <b>" + productType + "</b>!\n\n" +
                "Можете нажать на блюдо, чтобы увидеть подробное описание.\n\n"
                + "Вот, что мы с любовью приготовили для вас 😋:\n";
    }

    public void addAllProductsToMenu(StringBuilder menuText, List<String> productTypes) {
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
    }

    public String getErrorText(UUID userUUID) {
        return String.format("🌐Заходите на наш сайт [parktown.md](http://195.133.27.38/#menu/%s).\n" +
                "🎁Участвуйте в розыгрышах, получайте промокоды и смотрите за новостями!", userUUID);

    }

    public String getMessageAfterRegister(UUID userUUID) {
        return String.format("""
                Поздравляем! Теперь вы являетесь частью нашей семьи!
                
                🌐Заходите на наш сайт [parktown.md](http://195.133.27.38/#menu/%s).
                🎁Участвуйте в розыгрышах, получайте промокоды и смотрите за новостями!""", userUUID);
    }

    public String getWebSiteText(UUID userUUID) {
        return String
                .format("🌟 Привет! Добро пожаловать на наш сайт: [parktown.md](http://195.133.27.38/#menu/%s). Мы рады, что вы с нами! 😊", userUUID);
    }

    public String getDefaultMessage(UUID userUUID) {
        return String.format("""
                Неизвестная команда 🤯. Введите /help, чтобы увидеть доступные команды.
                
                Можете посмотреть наше меню /menu ☺ или сделать заказ у нас на сайте [parktown.md](http://195.133.27.38/#menu/%s).""", userUUID);
    }

    public String getUserInfo(User user) {
        TelegramUserEntity telegramUserEntity = user.getTelegramUserEntity();

        StringBuilder userInfoText = new StringBuilder();
        userInfoText.append("<i><b>Имя</b></i>: ").append(telegramUserEntity.getFirstname()).append("\n");
        userInfoText.append("<i><b>Никнейм</b></i>: ").append(telegramUserEntity.getUsername()).append("\n");
        userInfoText.append("<i><b>Сделано заказов</b></i>: ").append(orderService.getCountOrdersByUserChatId(telegramUserEntity.getChatId())).append("\n");
        userInfoText.append("<i><b>Дата регистрации в боте</b></i>: ").append(telegramUserEntity.getCreatedAt().toString()).append("\n");
        userInfoText.append("<i><b>Номер чата</b></i>: ").append(telegramUserEntity.getChatId()).append("\n");

        return userInfoText.toString();
    }

    public String getTextForSendingOtpCode(String otp) {
        StringBuilder textForSendingOtpCode = new StringBuilder();
        textForSendingOtpCode.append("Попытка входа в ваш аккаунт❗\n");
        textForSendingOtpCode.append("Если вы не совершали попытки входа то просто проигнорируйте это сообщение.\n\n");
        textForSendingOtpCode.append("Проверочный код для входа в аккаунт: `").append(otp).append("`\n");
        return textForSendingOtpCode.toString();
    }

    public String getTextForGlobalDiscount(Discount discount, UUID userUUID) {
        BigDecimal discountPercentage = discount.getDiscount();
        String code = discount.getCode();
        String description = discount.getDescription();

        ZoneId zoneId = ZoneId.of("Europe/Chisinau");
        ZonedDateTime validFromZoned = discount.getValidFrom().atZone(zoneId);
        ZonedDateTime validToZoned = discount.getValidTo().atZone(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, HH:mm", new Locale("ru"));
        String formattedValidFrom = validFromZoned.format(formatter);
        String formattedValidTo = validToZoned.format(formatter);

        String userLink = String.format("[parktown.md](http://195.133.27.38/#menu/%s)", userUUID);

        return new StringBuilder()
                .append("🔥 Внимание! Специальное предложение! 🔥\n\n")
                .append("🎉 Скидка ").append(discountPercentage).append("% на все заказы!\n\n")
                .append("📅 Акция действует с ").append(formattedValidFrom).append(" до ").append(formattedValidTo).append("\n\n")
                .append("🎟 Используйте промокод: `").append(code).append("`\n\n")
                .append(description).append("\n\n")
                .append("⚡ Не упустите шанс сэкономить! Заходите на наш сайт и заказывайте прямо сейчас: ").append(userLink)
                .toString();
    }

    public String getTextForProductDiscount(ProductDiscount productDiscount, UUID userUUID) {
        BigDecimal discountPercentage = productDiscount.getDiscount();
        String code = productDiscount.getCode();
        String description = productDiscount.getDescription();

        ZoneId zoneId = ZoneId.of("Europe/Chisinau");
        ZonedDateTime validFromZoned = productDiscount.getValidFrom().atZone(zoneId);
        ZonedDateTime validToZoned = productDiscount.getValidTo().atZone(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, HH:mm", new Locale("ru"));
        String formattedValidFrom = validFromZoned.format(formatter);
        String formattedValidTo = validToZoned.format(formatter);

        String userLink = String.format("[parktown.md](http://195.133.27.38/#menu/%s)", userUUID);

        Product product = productDiscount.getProduct();
        BigDecimal priceWithDiscount = product.getPrice()
                .multiply(BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100))))
                .setScale(2, RoundingMode.HALF_UP);  // Округление до двух знаков после запятой

        return new StringBuilder()
                .append("🔥 Внимание! Специальное предложение! 🔥\n\n")
                .append("🎉 Скидка ").append(discountPercentage).append("% на блюдо!\n\n")
                .append("💰 Вместо ").append(product.getPrice()).append(" леев всего ").append(priceWithDiscount).append(" леев\n")
                .append("📅 Акция действует с ").append(formattedValidFrom).append(" до ").append(formattedValidTo).append("\n\n")
                .append("🎟 Используйте промокод: `").append(code).append("`\n\n")
                .append(description).append("\n\n")
                .append("⚡ Не упустите шанс сэкономить! Заходите на наш сайт и заказывайте прямо сейчас: ").append(userLink)
                .toString();
    }

}
