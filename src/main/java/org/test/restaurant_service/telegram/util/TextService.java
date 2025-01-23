package org.test.restaurant_service.telegram.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.TelegramUserEntity;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public
class TextService {

    String adText =
            """
                    🍽️ <b>Время вкусных открытий!</b>
                    
                    ✨ Сегодня у нас для вас нечто особенное:
                    🍕 <b>Пицца недели:</b> Сырный взрыв — только 149 mdl!
                    🍹 <b>Коктейли:</b> Закажи два и получи третий в подарок!
                    
                    🎉 <i>Забронируйте столик прямо сейчас, чтобы не упустить шанс насладиться уникальными блюдами!</i>
                    
                    📲 Нажмите /menu, чтобы посмотреть всё меню!
                    
                    ❤️ С любовью, ваш ARNAUT's!""";

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
                    📢 Получать самые свежие новости о мероприятиях ARNAUT's
                    
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
                
                ❤️ С любовью, ваш ARNAUTS!""", telegramUserEntity.getFirstname());
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

    public String getErrorText(Long chatId) {
        return String.format("🌐Заходите на наш сайт https://arnauts.md/%d.\n" +
                "🎁Участвуйте в розыгрышах, получайте промокоды и смотрите за новостями!", chatId);

    }

    public String getMessageAfterRegister(Long chatId) {
        return String.format("""
                Поздравляем! Теперь вы являетесь частью нашей семьи!
                
                🌐Заходите на наш сайт [arnauts.md](https://arnauts.md/%s)
                🎁Участвуйте в розыгрышах, получайте промокоды и смотрите за новостями!""", chatId);
    }

    public String getWebSiteText(Long chatId) {
        return String
                .format("🌟 Привет! Добро пожаловать на наш сайт: [arnauts.md](https://arnauts.md/%s). Мы рады, что вы с нами! 😊", chatId);
    }

    public String getDefaultMessage(Long chatId) {
        return String.format("""
                Неизвестная команда 🤯. Введите /help, чтобы увидеть доступные команды.
                
                Можете посмотреть наше меню /menu ☺ или сделать заказ у нас на сайте [arnauts.md](https://arnauts.md/%s).""", chatId);
    }
}
