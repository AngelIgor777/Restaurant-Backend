package org.test.restaurant_service.service;


import org.test.restaurant_service.dto.response.admin.JwtResponse;
import org.test.restaurant_service.entity.Admin;
import org.test.restaurant_service.entity.User;


import java.util.List;
import java.util.UUID;

public interface JwtService {
    String generateUserAccessToken(Long chatId, List<String> roles);

    String extractToken();

    List<String> getRoles(String accessToken);

    public Long getChatId(String accessToken);

    org.test.restaurant_service.dto.response.JwtResponse generateUserAccessToken(UUID userUUID);

    JwtResponse generateJwtResponseForAdmin(Admin admin, User user);

    List<String> getRolesFromDisposableToken(String disposableToken);
}
