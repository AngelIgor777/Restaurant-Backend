package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.User;

public interface OtpService {
    void generateAndSendOtp(User user);

    String generateOtpForOrder();

    boolean verifyOtp(Long chatId, String otp);
}
