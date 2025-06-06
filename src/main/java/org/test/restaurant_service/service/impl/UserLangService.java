package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.repository.TelegramUserRepository;
import org.test.restaurant_service.repository.UserRepository;
import org.test.restaurant_service.service.LanguageService;
import org.test.restaurant_service.service.RoleService;

@Service
@RequiredArgsConstructor
public class UserLangService {
    private final LanguageService languageService;

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final TelegramUserRepository telegramUserRepository;


    @Transactional(rollbackFor = Exception.class)
    public void registerUser(Update update, String userPhotoUrl) {
        Message message = update.getMessage();
        User user = message.getFrom();

        TelegramUserEntity telegramUser = TelegramUserEntity.builder()
                .chatId(message.getChatId())
                .firstname(user.getFirstName())
                .username(user.getUserName())
                .language(languageService.getLanguageByCode("ru"))
                .build();

        //its ensure because photo can be null
        if (userPhotoUrl != null) {
            telegramUser.setPhotoUrl(userPhotoUrl);
        }


        org.test.restaurant_service.entity.User userEntity =
                org.test.restaurant_service.entity.User.builder()
                        .telegramUserEntity(telegramUser)
                        .build();

        roleService.ensureUserHasRole(userEntity, RoleName.ROLE_USER);
        TelegramUserEntity save = telegramUserRepository.save(telegramUser);
        userEntity.setTelegramUserEntity(save);
        userRepository.save(userEntity);
    }
}
