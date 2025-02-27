package org.test.restaurant_service.telegram.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.service.OrderService;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.ProductTypeTranslationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Service
public
class TextUtil {

    private final OrderService orderService;
    private final ProductService productService;
    private final ProductTypeTranslationService productTypeTranslationService;

    private String adTextRu =
            """
                    🍽️ <b>Время вкусных открытий!</b>
                    
                    ✨ Сегодня у нас для вас нечто особенное:
                    🍕 <b>Пицца недели:</b> Сырный взрыв — только 149 mdl!
                    🍹 <b>Коктейли:</b> Закажи два и получи третий в подарок!
                    
                    🎉 <i>Забронируйте столик прямо сейчас, чтобы не упустить шанс насладиться уникальными блюдами!</i>
                    
                    📲 Нажмите /menu, чтобы посмотреть всё меню!
                    
                    ❤️ С любовью, ваш PARK TOWN""";


    private final String adTextRo = """
            🍽️ <b>Timpul descoperirilor delicioase!</b>
            
            ✨ Astăzi avem ceva special pentru tine:
            🍕 <b>Pizza săptămânii:</b> Explozie de brânză — doar 149 lei!
            🍹 <b>Cocktailuri:</b> Comandă două și primești al treilea gratuit!
            
            📲 Apasă /menu pentru a vedea tot meniul!
            
            ❤️ Cu drag, al tău PARK TOWN""";


    private final String helpTextRu =
            """
                    📖 <b>Доступные команды:</b>
                    
                    🚀 /start - <i>Запуск бота</i>
                    ❓ /help - <i>Список доступных команд</i>
                    ℹ️ /info - <i>Информация о боте</i>
                    🍽️ /menu - <i>Показать меню</i>
                    📁 /about - <i>Показать информацию профиля</i>
                    
                    ✨ Используйте команды, чтобы сделать ваше настроение более радостным!
                    """;

    private final String helpTextRo =
            """
                    📖 <b>Comenzi disponibile:</b>
                    
                    🚀 /start - <i>Pornește botul</i>
                    ❓ /help - <i>Lista comenzilor disponibile</i>
                    ℹ️ /info - <i>Informații despre bot</i>
                    🍽️ /menu - <i>Afișează meniul</i>
                    📁 /about - <i>Afișează informațiile profilului</i>
                    
                    ✨ Utilizați comenzile pentru a vă face ziua mai frumoasă!
                    """;


    private final String infoTextRu =
            """
                    🤖 <b>Добро пожаловать!</b>
                    
                    Этот бот создан, чтобы сделать вашу жизнь проще и приятнее! 🌟
                    
                    С его помощью вы можете:
                    📝 Заказать еду на нашем сайте
                    📢 Получать самые свежие новости о мероприятиях PARK TOWN
                    
                    ✨ Мы всегда рады быть вам полезными!
                    """;

    private final String infoTextRo =
            """
                    🤖 <b>Bine ați venit!</b>
                    
                    Acest bot este creat pentru a vă face viața mai ușoară și mai plăcută! 🌟
                    
                    Cu ajutorul său puteți:
                    📝 Să comandați mâncare de pe site-ul nostru
                    📢 Să primiți cele mai recente știri despre evenimentele PARK TOWN
                    
                    ✨ Suntem mereu bucuroși să vă fim de ajutor!
                    """;


    public TextUtil(OrderService orderService, @Qualifier("productServiceWithS3Impl") ProductService productService, ProductTypeTranslationService productTypeTranslationService) {
        this.orderService = orderService;
        this.productService = productService;
        this.productTypeTranslationService = productTypeTranslationService;
    }

    public String getAdTextByLanguage(String language) {
        return language.equalsIgnoreCase("ro") ? adTextRo : adTextRu;
    }

    public String getInfoText(String language) {
        return "ro".equalsIgnoreCase(language) ? infoTextRo : infoTextRu;
    }


    public String getHelpText(String language) {
        return "ro".equalsIgnoreCase(language) ? helpTextRo : helpTextRu;
    }

    public StringBuilder getProductText(ProductResponseDTO productResponse) {
        StringBuilder productText = new StringBuilder();
        LocalTime cookingTime = productResponse.getCookingTime();
        productText.append("🍴 <b>Блюдо:</b> ").append(productResponse.getName()).append("\n");
        productText.append("✨ <i>Описание:</i> ").append(productResponse.getDescription()).append("\n");
        productText.append("📂 <i>Категория:</i> ").append(productResponse.getTypeName()).append("\n");
        productText.append("💰 <b>Стоимость:</b> ").append(productResponse.getPrice()).append(" лей\n");
        if (cookingTime != null) {
            productText.append("⏱️ <b>Время приготовления:</b> ")
                    .append(cookingTime.getMinute()).append(" минут\n");
        }
        productText.append("\n🍽️ Наслаждайтесь изысканным вкусом и уютной атмосферой! ❤️");


        return productText;
    }

    public StringBuilder getProductTranslationRoText(ProductResponseDTO productResponse, ProductTranslation productTranslation, ProductTypeTranslationResponseDTO productTypeTranslationResponseDTO) {
        StringBuilder productText = new StringBuilder();
        LocalTime cookingTime = productResponse.getCookingTime();
        productText.append("🍴 <b>Fel de mâncare:</b> ").append(productTranslation.getName()).append("\n");
        productText.append("✨ <i>Descriere:</i> ").append(productTranslation.getDescription()).append("\n");
        productText.append("📂 <i>Categorie:</i> ").append(productTypeTranslationResponseDTO.getName()).append("\n");
        productText.append("💰 <b>Preț:</b> ").append(productResponse.getPrice()).append(" lei\n");
        if (cookingTime != null) {
            productText.append("⏱️ <b>Timp de preparare:</b> ")
                    .append(cookingTime.getMinute()).append(" minute\n");
        }
        productText.append("\n🍽️ Bucurați-vă de gustul rafinat și atmosfera confortabilă! ❤️");


        return productText;
    }


    public String getCaptionForUser(TelegramUserEntity telegramUserEntity) {
        String language = telegramUserEntity.getLanguage().getCode();
        if ("ro".equalsIgnoreCase(language)) {
            return String.format("""
                    %s Salut! 😎 Avem ceva nou pentru tine!
                    🍽️<b>Timpul descoperirilor delicioase!</b>
                    ✨ Pizza săptămânii: Explozie de brânză — doar 149 lei!
                    📲 Apasă /menu pentru a vedea meniul!
                    ❤️ Cu drag, al tău PARK TOWN""", telegramUserEntity.getFirstname());
        } else {
            return String.format("""
                    %s Привет! 😎 У нас для тебя что-то новенькое!
                    🍽️<b>Время вкусных открытий!</b>
                    ✨ Пицца недели: Сырный взрыв — только 149 лей!
                    📲 Нажмите /menu, чтобы посмотреть всё меню!
                    ❤️ С любовью, ваш PARK TOWN""", telegramUserEntity.getFirstname());
        }
    }


    public String getProductTypeTextByType(String productType, String language) {
        if ("ro".equalsIgnoreCase(language)) {
            return "🍽️ Ai ales categoria <b>" + productType + "</b>!\n\n" +
                    "Apasă pe un fel de mâncare pentru a vedea descrierea detaliată.\n\n" +
                    "Iată ce am pregătit cu drag pentru tine 😋:\n";
        } else {
            return "🍽️ Вы выбрали категорию <b>" + productType + "</b>!\n\n" +
                    "Можете нажать на блюдо, чтобы увидеть подробное описание.\n\n" +
                    "Вот, что мы с любовью приготовили для вас 😋:\n";
        }
    }


    public void addAllProductsToMenu(StringBuilder menuText, List<String> productTypes, String language) {
        switch (language) {
            case "ro" -> {
                menuText.append("🍽️ <i><b>Bine ați venit în meniul nostru confortabil!</b></i> \n\n")
                        .append("✨ Aici veți găsi preparate rafinate care vă vor oferi plăcere și bucurie! ✨\n\n");
            }
            case "ru" -> {
                menuText.append("🍽️ <i><b>Добро пожаловать в наше уютное меню!</b></i> \n\n")
                        .append("✨ Здесь вы найдёте изысканные блюда, которые подарят вам наслаждение и радость! ✨\n\n");
            }
        }

        for (int i = 1; i <= productTypes.size(); i++) {
            menuText.append("\uD83D\uDD38 <b>")
                    .append(i).append(". ")
                    .append(productTypes.get(i - 1))
                    .append("</b> \n")
                    .append("   ───────────────\n");
        }

        switch (language) {
            case "ro" -> menuText.append("\n💌 Vă mulțumim că ne alegeți! Gustul dvs. este grija noastră! 💌\n")
                    .append("🎉 <i>Introduceți numărul categoriei sau alegeți din meniul de mai jos!</i> 🎉\n");
            case "ru" -> menuText.append("\n💌 Спасибо, что выбираете нас! Ваш вкус — наша забота! 💌\n")
                    .append("🎉 <i>Введите номер категории или выберите из меню ниже!</i> 🎉\n");
        }
    }

    public String getErrorText(UUID userUUID, String language) {
        return switch (language) {
            case "ro" -> String.format("🌐Vizitați site-ul nostru [parktown.md](http://195.133.27.38/#menu/%s).\n" +
                    "🎁Participați la tombole, obțineți coduri promoționale și urmăriți știrile!", userUUID);
            case "ru" -> String.format("🌐Заходите на наш сайт [parktown.md](http://195.133.27.38/#menu/%s).\n" +
                    "🎁Участвуйте в розыгрышах, получайте промокоды и смотрите за новостями!", userUUID);
            default -> "";
        };
    }

    public String getMessageAfterRegister(UUID userUUID, String language) {
        return switch (language) {
            case "ro" -> String.format("""
                    Felicitări! Acum faceți parte din familia noastră!
                    
                    🌐Vizitați site-ul nostru [parktown.md](http://195.133.27.38/#menu/%s).
                    🎁Participați la tombole și urmăriți știrile!
                    """, userUUID);
            case "ru" -> String.format("""
                    Поздравляем! Теперь вы являетесь частью нашей семьи!
                    
                    🌐Заходите на наш сайт [parktown.md](http://195.133.27.38/#menu/%s).
                    🎁Участвуйте в розыгрышах и смотрите за новостями!
                    """, userUUID);
            default -> "";
        };
    }

    public String getWebSiteText(UUID userUUID, String language) {
        return switch (language) {
            case "ro" ->
                    String.format("🌟 Salut! Bine ați venit pe site-ul nostru: [parktown.md](http://195.133.27.38/#menu/%s). Suntem bucuroși că sunteți cu noi! 😊", userUUID);
            case "ru" ->
                    String.format("🌟 Привет! Добро пожаловать на наш сайт: [parktown.md](http://195.133.27.38/#menu/%s). Мы рады, что вы с нами! 😊", userUUID);
            default -> "";
        };
    }

    public String getDefaultMessage(UUID userUUID, String language) {
        return switch (language) {
            case "ro" -> String.format("""
                    Comandă necunoscută 🤯. Introduceți /help pentru a vedea comenzile disponibile.
                    
                    Puteți consulta meniul nostru /menu ☺ sau comandați de pe site-ul nostru [parktown.md](http://195.133.27.38/#menu/%s).
                    """, userUUID);
            case "ru" -> String.format("""
                    Неизвестная команда 🤯. Введите /help, чтобы увидеть доступные команды.
                    
                    Можете посмотреть наше меню /menu ☺ или сделать заказ у нас на сайте [parktown.md](http://195.133.27.38/#menu/%s).
                    """, userUUID);
            default -> "";
        };
    }

    public String getUserInfo(User user) {
        TelegramUserEntity telegramUserEntity = user.getTelegramUserEntity();
        String language = telegramUserEntity.getLanguage().getCode();
        StringBuilder userInfoText = new StringBuilder();

        switch (language) {
            case "ro":
                userInfoText.append("<i><b>Nume</b></i>: ").append(telegramUserEntity.getFirstname()).append("\n");
                userInfoText.append("<i><b>Nickname</b></i>: ").append(telegramUserEntity.getUsername()).append("\n");
                userInfoText.append("<i><b>Număr de comenzi făcute</b></i>: ").append(orderService.getCountOrdersByUserChatId(telegramUserEntity.getChatId())).append("\n");
                break;

            case "ru":
            default:
                userInfoText.append("<i><b>Имя</b></i>: ").append(telegramUserEntity.getFirstname()).append("\n");
                userInfoText.append("<i><b>Никнейм</b></i>: ").append(telegramUserEntity.getUsername()).append("\n");
                userInfoText.append("<i><b>Сделано заказов</b></i>: ").append(orderService.getCountOrdersByUserChatId(telegramUserEntity.getChatId())).append("\n");
                break;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime createdAt = telegramUserEntity.getCreatedAt();

        switch (language) {
            case "ro":
                userInfoText.append("<i><b>Data înregistrării în bot</b></i>: ")
                        .append(createdAt.format(formatter))
                        .append("\n");
                userInfoText.append("<i><b>Număr chat</b></i>: ").append(telegramUserEntity.getChatId()).append("\n");
                break;

            case "ru":
            default:
                userInfoText.append("<i><b>Дата регистрации в боте</b></i>: ")
                        .append(createdAt.format(formatter))
                        .append("\n");
                userInfoText.append("<i><b>Номер чата</b></i>: ").append(telegramUserEntity.getChatId()).append("\n");
                break;
        }

        return userInfoText.toString();
    }


    public String getTextForSendingOtpCode(String otp, String language) {
        return switch (language) {
            case "ro" -> "Încercare de conectare la contul dvs❗\n" +
                    "Dacă nu ați încercat să vă conectați, ignorați acest mesaj.\n\n" +
                    "Codul de verificare pentru conectare: " + otp + "\n";
            case "ru" -> "Попытка входа в ваш аккаунт❗\n" +
                    "Если вы не совершали попытки входа, проигнорируйте это сообщение.\n\n" +
                    "Проверочный код для входа в аккаунт: " + otp + "\n";
            default -> "";
        };
    }

    public String getTextForGlobalDiscount(Discount discount, UUID userUUID, String language) {
        BigDecimal discountPercentage = discount.getDiscount();
        String code = discount.getCode();
        String description = discount.getDescription();

        ZoneId zoneId = ZoneId.of("Europe/Chisinau");
        ZonedDateTime validFromZoned = discount.getValidFrom().atZone(zoneId);
        ZonedDateTime validToZoned = discount.getValidTo().atZone(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, HH:mm", new Locale(language.equals("ro") ? "ro" : "ru"));
        String formattedValidFrom = validFromZoned.format(formatter);
        String formattedValidTo = validToZoned.format(formatter);

        String userLink = String.format("[parktown.md](http://195.133.27.38/#menu/%s)", userUUID);

        if ("ro".equals(language)) {
            return new StringBuilder()
                    .append("🔥 Atenție! Ofertă specială! 🔥\n\n")
                    .append("🎉 Reducere de ").append(discountPercentage).append("% la toate comenzile!\n\n")
                    .append("📅 Promoție valabilă de la ").append(formattedValidFrom).append(" până la ").append(formattedValidTo).append("\n\n")
                    .append("🎟 Utilizați codul promoțional: `").append(code).append("`\n\n")
                    .append(description).append("\n\n")
                    .append("⚡ Nu ratați șansa de a economisi! Vizitați site-ul nostru și comandați acum: ").append(userLink)
                    .toString();
        } else {
            return new StringBuilder()
                    .append("🔥 Внимание! Специальное предложение! 🔥\n\n")
                    .append("🎉 Скидка ").append(discountPercentage).append("% на все заказы!\n\n")
                    .append("📅 Акция действует с ").append(formattedValidFrom).append(" до ").append(formattedValidTo).append("\n\n")
                    .append("🎟 Используйте промокод: `").append(code).append("`\n\n")
                    .append(description).append("\n\n")
                    .append("⚡ Не упустите шанс сэкономить! Заходите на наш сайт и заказывайте прямо сейчас: ").append(userLink)
                    .toString();
        }
    }

    public String getTextForProductDiscount(ProductDiscount productDiscount, UUID userUUID, String language) {
        BigDecimal discountPercentage = productDiscount.getDiscount();
        String code = productDiscount.getCode();
        String description = productDiscount.getDescription();

        ZoneId zoneId = ZoneId.of("Europe/Chisinau");
        ZonedDateTime validFromZoned = productDiscount.getValidFrom().atZone(zoneId);
        ZonedDateTime validToZoned = productDiscount.getValidTo().atZone(zoneId);

        Locale locale = language.equals("ro") ? new Locale("ro") : new Locale("ru");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, HH:mm", locale);
        String formattedValidFrom = validFromZoned.format(formatter);
        String formattedValidTo = validToZoned.format(formatter);

        String userLink = String.format("[parktown.md](http://195.133.27.38/#menu/%s)", userUUID);

        Product product = productDiscount.getProduct();
        String name = product.getName();
        BigDecimal priceWithDiscount = product.getPrice()
                .multiply(BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100))))
                .setScale(2, RoundingMode.HALF_UP);

        if (language.equals("ro")) {
            return new StringBuilder()
                    .append("🔥 Atenție! Ofertă specială! 🔥\n\n")
                    .append("🎉 Reducere de ").append(discountPercentage).append("% la felul de mâncare!\n\n")
                    .append("💰 În loc de ").append(product.getPrice()).append(" lei doar ").append(priceWithDiscount).append(" lei\n")
                    .append("📅 Oferta este valabilă din ").append(formattedValidFrom).append(" până la ").append(formattedValidTo).append("\n\n")
                    .append("🎟 Folosiți codul promoțional: `").append(code).append("`\n\n")
                    .append(description).append("\n\n")
                    .append("⚡ Nu ratați ocazia de a economisi! Vizitați site-ul nostru și comandați acum: ").append(userLink)
                    .toString();
        } else {
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


    public String getTopWeekProducts(UUID userUUID, String language) {
        String userLink = String.format("[parktown.md](http://195.133.27.38/#menu/%s)", userUUID);

        Pageable pageable = PageRequest.of(0, 10);
        List<ProductResponseDTO> top10WeekProducts = productService.getTop10WeekProducts(pageable);

        StringBuilder stringBuilder = new StringBuilder();
        if (language.equals("ro")) {
            stringBuilder.append("🔥 Top-10 cele mai populare feluri de mâncare ale săptămânii! 🔥\n\n");
        } else {
            stringBuilder.append("🔥 Топ-10 самых популярных блюд недели! 🔥\n\n");
        }

        int index = 1;
        for (ProductResponseDTO product : top10WeekProducts) {
            stringBuilder.append(index).append(". ")
                    .append("🍽 ").append(product.getName()).append(" - ")
                    .append(product.getPrice()).append(" lie")
                    .append("\n⏳ ")
                    .append(language.equals("ro") ? "Timp de preparare: " : "Время приготовления: ")
                    .append(product.getCookingTime() != null ? product.getCookingTime() : (language.equals("ro") ? "verificați la chelner" : "уточните у официанта"))
                    .append("\n✨ ").append(getHotSlogan(language))
                    .append("\n\n");
            index++;
        }

        stringBuilder.append(language.equals("ro") ? "💥 Grăbiți-vă să încercați! 🍔🔥 Vizitați site-ul nostru și comandați acum: "
                : "💥 Спешите попробовать! 🍔🔥Заходите на наш сайт и заказывайте прямо сейчас: ").append(userLink);
        return stringBuilder.toString();
    }

    private String getHotSlogan(String language) {
        List<String> slogansRo = List.of(
                "Încearcă și te vei îndrăgosti! 💕",
                "Ideal pentru gurmanzi! 🍷",
                "Gust care cucerește! 🌟",
                "Nu-ți refuza plăcerea! 😋",
                "Este un hit! 🔥"
        );

        List<String> slogansRu = List.of(
                "Попробуй и влюбись! 💕",
                "Идеально для гурманов! 🍷",
                "Вкус, который покоряет! 🌟",
                "Не отказывай себе в удовольствии! 😋",
                "Это хит! 🔥"
        );

        return language.equals("ro") ? slogansRo.get(new Random().nextInt(slogansRo.size()))
                : slogansRu.get(new Random().nextInt(slogansRu.size()));
    }

    public String getTextForConnection(String name, String email, String event, String phoneNumber, String message, String language) {
        StringBuilder stringBuilder = new StringBuilder();

        if (language.equals("ro")) {
            stringBuilder.append("📞 <i>Cerere nouă!</i>\n\n");
            stringBuilder.append("👤 <b>Nume:</b> ").append(name).append("\n");
            stringBuilder.append("✉️ <b>Email:</b> ").append(email).append("\n");
            stringBuilder.append("🎉 <b>Eveniment:</b> ").append(event).append("\n");
            stringBuilder.append("📞 <b>Telefon:</b> ").append(phoneNumber).append("\n");
            stringBuilder.append("📝 <b>Mesaj:</b> ").append(message).append("\n\n");
            stringBuilder.append("⚡ <i>Vă rugăm să contactați utilizatorul cât mai curând posibil!</i>");
        } else {
            stringBuilder.append("📞 <i>Новая заявка!</i>\n\n");
            stringBuilder.append("👤 <b>Имя:</b> ").append(name).append("\n");
            stringBuilder.append("✉️ <b>Email:</b> ").append(email).append("\n");
            stringBuilder.append("🎉 <b>Событие:</b> ").append(event).append("\n");
            stringBuilder.append("📞 <b>Телефон:</b> ").append(phoneNumber).append("\n");
            stringBuilder.append("📝 <b>Сообщение:</b> ").append(message).append("\n\n");
            stringBuilder.append("⚡ <i>Пожалуйста, свяжитесь с пользователем как можно скорее!</i>");
        }

        return stringBuilder.toString();
    }


}
