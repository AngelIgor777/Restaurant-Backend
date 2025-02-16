package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.Role;

import java.util.List;

public interface JwtService {
    String generateUserAccessToken(Long chatId, List<String> roles);
    String extractToken();
    List<String> getRoles(String accessToken);
}
