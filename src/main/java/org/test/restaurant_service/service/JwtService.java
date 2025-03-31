package org.test.restaurant_service.service;


import org.test.restaurant_service.dto.response.JwtResponse;

import java.util.List;
import java.util.UUID;

public interface JwtService {
    String generateUserAccessToken(Long chatId, List<String> roles);

    String extractToken();

    List<String> getRoles(String accessToken);

    public Long getChatId(String accessToken);

    JwtResponse generateUserAccessToken(UUID userUUID);
}
