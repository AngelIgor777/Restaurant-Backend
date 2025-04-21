package org.test.restaurant_service.service;

public interface OtpService {

    String generateOtpForOrder();

    boolean verifyOtp(Long chatId, String otp);
}
