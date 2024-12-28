package org.test.restaurant_service.service;

import org.telegram.telegrambots.meta.api.objects.User;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.Otp;

import java.util.List;


public interface OtpService {
    Otp generateAndSaveOtp(Long chatId);

    JwtResponse verifyOtpCode(String otpCode);

    Otp save(Otp otp);

    boolean existByChatId(Long chatId);

    List<Otp> getAll();
}
