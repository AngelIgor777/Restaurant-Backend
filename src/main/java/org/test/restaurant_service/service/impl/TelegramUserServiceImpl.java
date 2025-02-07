package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.Role;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.repository.RoleRepository;
import org.test.restaurant_service.repository.TelegramUserRepository;
import org.test.restaurant_service.repository.UserRepository;
import org.test.restaurant_service.service.RoleService;
import org.test.restaurant_service.service.TelegramUserService;

import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public org.test.restaurant_service.entity.User registerUser(Update update) {
        Message message = update.getMessage();
        User user = message.getFrom();

        TelegramUserEntity telegramUser = TelegramUserEntity.builder()
                .chatId(message.getChatId())
                .firstname(user.getFirstName())
                .username(user.getUserName())
                .build();

        org.test.restaurant_service.entity.User userEntity =
                org.test.restaurant_service.entity.User.builder()
                        .telegramUserEntity(telegramUser)
                        .build();

        roleService.ensureUserHasRole(userEntity, RoleName.ROLE_ADMIN);
        TelegramUserEntity save = telegramUserRepository.save(telegramUser);
        userEntity.setTelegramUserEntity(save);
        return userRepository.save(userEntity);
    }

    @Override
    public TelegramUserEntity save(TelegramUserEntity telegramUserEntity) {
        return telegramUserRepository.save(telegramUserEntity);
    }

    @Override
    public boolean existByChatId(Long chatId) {
        return telegramUserRepository.existsTelegramUserEntitiesByChatId(chatId);
    }

    @Override
    public List<TelegramUserEntity> getAll() {
        return telegramUserRepository.findAll();
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

    private List<String> getUserRoles() {
        List<String> roles = new ArrayList<>();

        String roleUser = RoleName.ROLE_USER.toString();
        roles.add(roleUser);
        return roles;
    }
}
