package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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

    public boolean isValidDisposableToken(Authentication auth) {

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DISPOSABLE"));
    }

    public boolean userIsAdminOrModerator(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMod = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));


        return isAdmin || isMod;
    }

    private boolean isValidRoles(List<String> roles) {
        return roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MODERATOR");
    }

    //need for identify user request for receive his data or admin request
    public boolean userIsOwnerOrModeratorOrAdmin(Authentication auth, UUID userUUID) {
        Long chatId = Long.valueOf(auth.getName());

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMod = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        User user = userService.findByUUID(userUUID);
        Long userChatId = user.getTelegramUserEntity().getChatId();

        return isAdmin || isMod || userChatId.equals(chatId);
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
        return userService.findByChatId(chatId);
    }

    public boolean userIsModerator(Long chatId) {
        return userService.findByChatId(chatId).isModerator();
    }

    //if moderator - his can add role only cook. but if user have admin role he can add all roles
    public boolean checkPermissions(Authentication auth, RoleName roleName) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));


        if (isAdmin) {
            return true;
        } else if (!isAdmin && roleName.equals(RoleName.ROLE_MODERATOR)) {
            return false;
        } else {
            return false;
        }
    }

    public boolean userIsActivated(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ACTIVATION"));
    }
}
