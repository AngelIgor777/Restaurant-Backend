package org.test.restaurant_service.service;


import org.test.restaurant_service.dto.response.admin.JwtResponse;
import org.test.restaurant_service.entity.Admin;
import org.test.restaurant_service.entity.User;

import java.util.List;

public interface JwtService {
    String generateUserAccessToken(Long chatId, List<String> roles);

    String extractToken();

    List<String> getRoles(String accessToken);

    JwtResponse generateJwtResponseForAdmin(Admin admin, User user);

    List<String> getRolesFromDisposableToken(String disposableToken);
}
