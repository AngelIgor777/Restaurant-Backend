package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.repository.TelegramUserRepository;
import org.test.restaurant_service.repository.UserRepository;
import org.test.restaurant_service.service.LanguageService;
import org.test.restaurant_service.service.RoleService;
import org.test.restaurant_service.service.TelegramUserService;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;




    @Override
    public TelegramUserEntity save(TelegramUserEntity telegramUserEntity) {
        return telegramUserRepository.save(telegramUserEntity);
    }

    @Override
    public boolean existByChatId(Long chatId) {
        return telegramUserRepository.existsTelegramUserEntitiesByChatId(chatId);
    }

    @Override
    public Page<TelegramUserEntity> getAll(Pageable pageable) {
        return telegramUserRepository.findAll(pageable);
    }

    @Override
    public TelegramUserEntity save(UserRegistrationDTO user) {
        User userEntity = user.getUser();
        String username = userEntity.getUserName() != null ? userEntity.getUserName() : "unknown";
        String firstname = userEntity.getFirstName();

        TelegramUserEntity telegramUserEntity = TelegramUserEntity.builder()
                .chatId(user.getChatId())
                .username(username)
                .firstname(firstname)
                .build();
        save(telegramUserEntity);

        return telegramUserEntity;
    }

    @Override
    public TelegramUserEntity get(Update update) {
        Long chatId = update.getMessage().getChatId();
        return getByChatId(chatId);
    }

    @Override
    public TelegramUserEntity getByChatId(Long chatId) {
        return telegramUserRepository.findTelegramUserEntityByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Telegram-User with " + chatId + " not found"));
    }

    private List<String> getUserRoles() {
        List<String> roles = new ArrayList<>();

        String roleUser = RoleName.ROLE_USER.toString();
        roles.add(roleUser);
        return roles;
    }
}
