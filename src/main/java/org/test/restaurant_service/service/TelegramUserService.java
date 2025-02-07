package org.test.restaurant_service.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.TelegramUserEntity;

import java.util.List;


public interface TelegramUserService {
    org.test.restaurant_service.entity.User registerUser(Update chatId);

    TelegramUserEntity save(TelegramUserEntity telegramUserEntity);

    boolean existByChatId(Long chatId);

    List<TelegramUserEntity> getAll();

    TelegramUserEntity save(UserRegistrationDTO user);
}
