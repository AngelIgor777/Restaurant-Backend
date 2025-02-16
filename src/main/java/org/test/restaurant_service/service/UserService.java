package org.test.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.test.restaurant_service.entity.User;

import java.util.UUID;

public interface UserService {

    User save(User user);

    User findByUUID(UUID uuid);

    User findByChatId(Long chatId);

    Page<User> getAll(Pageable pageable);
}
