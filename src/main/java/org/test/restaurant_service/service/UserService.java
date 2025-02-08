package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.User;

import java.util.UUID;

public interface UserService {

    User save(User user);

    User findByUUID(UUID uuid);

    User findByChatId(Long chatId);
}
