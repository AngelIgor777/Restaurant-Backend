package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Role;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.util.KeyUtil;

import java.util.List;
import java.util.UUID;

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

    //need for identify user request for receive his data or admin request
    public boolean userIsOwnerOrModeratorOrAdmin(String accessToken, UUID userUUID) {
        Long chatId = jwtService.getChatId(accessToken);
        User user = userService.findByUUID(userUUID);
        Long userChatId = user.getTelegramUserEntity().getChatId();
        List<String> roles = jwtService.getRoles(accessToken);
        return roles.contains(RoleName.ROLE_ADMIN.name())
                || roles.contains(RoleName.ROLE_MODERATOR.name())
                || (userChatId.equals(chatId));
    }

    public boolean userIsAdmin(Long chatId) {
        User user = userService.findByChatId(chatId);
        return user.isAdmin();
    }

    public boolean userIsAdmin(String accessToken) {
        User user = getUserByAccessToken(accessToken);
        return user.isAdmin();
    }

    private User getUserByAccessToken(String accessToken) {
        Long chatId = jwtService.getChatId(accessToken);
        User user = userService.findByChatId(chatId);
        return user;
    }

    public boolean userIsModerator(Long chatId) {
        User user = userService.findByChatId(chatId);
        return user.isModerator();
    }

    //if moderator - his can add role only cook. but if user have admin role he can add all roles
    public boolean checkPermissions(String accessToken, RoleName roleName) {
        List<RoleName> roles = getUserByAccessToken(accessToken).getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList();
        if (roles.contains(RoleName.ROLE_ADMIN)) {
            return true;
        } else if (!roles.contains(RoleName.ROLE_ADMIN) && roleName.equals(RoleName.ROLE_MODERATOR)) {
            return false;
        } else {
            return false;
        }
    }
}
