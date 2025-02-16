package org.test.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.TelegramUserEntity;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUserEntity, Integer> {


    boolean existsTelegramUserEntitiesByChatId(Long chatId);
    Optional<TelegramUserEntity> findTelegramUserEntityByChatId(Long chatId);

    Page<TelegramUserEntity> findAll(Pageable pageable);
}
