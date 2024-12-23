package org.test.restaurant_service.service;

import org.telegram.telegrambots.meta.api.objects.User;
import org.test.restaurant_service.entity.Otp;

public interface OtpService {
    Otp generateAndSaveOtp(Integer chatId, User username);
}
