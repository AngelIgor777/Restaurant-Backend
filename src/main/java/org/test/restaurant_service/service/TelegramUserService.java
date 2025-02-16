package org.test.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;
import org.test.restaurant_service.entity.TelegramUserEntity;

import java.util.List;


public interface TelegramUserService {
    org.test.restaurant_service.entity.User registerUser(Update chatId, String photoUrl);

    TelegramUserEntity save(TelegramUserEntity telegramUserEntity);

    boolean existByChatId(Long chatId);

    Page<TelegramUserEntity> getAll(Pageable pageable);

    TelegramUserEntity save(UserRegistrationDTO user);

    TelegramUserEntity get(Update update);

}
