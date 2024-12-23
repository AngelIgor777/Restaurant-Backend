package org.test.restaurant_service.service;

import java.util.List;
import java.util.UUID;

public interface JwtService {
    String generateUserAccessToken(Long chatId, List<String> roles);
}
