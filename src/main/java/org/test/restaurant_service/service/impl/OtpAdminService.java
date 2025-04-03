package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.OtpCode;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.OtpRepository;
import org.test.restaurant_service.telegram.handling.TelegramBot;
import org.test.restaurant_service.telegram.util.TextUtil;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpAdminService {
    private final TelegramBot telegramBot;
    private final TextUtil textUtil;
    private final OtpRepository otpRepository;

    public void generateAndSendOtp(User user) {
        otpRepository.findExistCode(user.getTelegramUserEntity().getChatId())
                .ifPresent(otpRepository::delete);
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // Create new OTP record
        OtpCode otpCode = new OtpCode();
        otpCode.setUser(user);
        otpCode.setOtpCode(otp);
        otpCode.setExpiresAt(expiresAt);
        otpRepository.save(otpCode);
        String otpMessage = textUtil.getTextForSendingOtpCode(otp, user.getTelegramUserEntity().getLanguage().getCode());
        telegramBot.sendMessageWithMarkdown(user.getTelegramUserEntity().getChatId(), otpMessage);
    }
}
