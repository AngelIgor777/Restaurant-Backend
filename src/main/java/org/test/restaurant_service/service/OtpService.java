package org.test.restaurant_service.service;

import org.telegram.telegrambots.meta.api.objects.User;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.Otp;


public interface OtpService {
    Otp generateAndSaveOtp(Long chatId, User username);

    JwtResponse verifyOtpCode(String otpCode);

    Otp save(Otp otp);
}
