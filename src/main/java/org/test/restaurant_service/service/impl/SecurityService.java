package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.util.KeyUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;
    private final JwtService jwtService;

    public boolean authenticateAdmin(String adminCode1, String adminCode2) {
        return (KeyUtil.getAdminCode1().equals(adminCode1) && KeyUtil.getAdminCode2().equals(adminCode2));
    }

    public boolean userIsAdminOrModerator(Long chatId) {
        User user = userService.findByChatId(chatId);
        return user.isAdminOrModerator();
    }

    public boolean userIsAdminDisposableKeyOwner(String disposableKey) {
        List<String> rolesFromDisposableToken = jwtService.getRolesFromDisposableToken(disposableKey);
        return isValidRoles(rolesFromDisposableToken);
    }

    public boolean userIsAdminOrModerator(String accessToken) {
        List<String> roles = jwtService.getRoles(accessToken);
        return isValidRoles(roles);
    }

    private boolean isValidRoles(List<String> roles) {
        return roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MODERATOR");
    }

    public boolean userIsAdmin(Long chatId) {
        User user = userService.findByChatId(chatId);
        return user.isAdmin();
    }

    public boolean userIsModerator(Long chatId) {
        User user = userService.findByChatId(chatId);
        return user.isModerator();
    }
}
