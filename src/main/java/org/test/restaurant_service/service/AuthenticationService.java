package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.response.JwtResponse;

public interface AuthenticationService {


    JwtResponse authenticate(Long chatId, String otp);
}
