package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.User;

public interface OtpService {
    void generateAndSendOtp(User user);

    boolean verifyOtp(Long chatId, String otp);
}
