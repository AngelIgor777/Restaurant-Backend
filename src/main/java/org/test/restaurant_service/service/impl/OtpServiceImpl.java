package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.OtpCode;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.OtpRepository;
import org.test.restaurant_service.service.OrderService;
import org.test.restaurant_service.service.OtpService;
import org.test.restaurant_service.telegram.handling.TelegramBot;
import org.test.restaurant_service.telegram.util.TextUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final OrderService orderService;

    @Override
    public String generateOtpForOrder() {
        String otp = generate2DOtp();

        int i = 0;
        while (orderService.existsByOtp(otp)) {
            otp = generate2DOtp();
            i++;
            if (i > 99) {
                otp = generate3DOtp();
                i = 0;
            }
        }
        return otp;
    }

    private static String generate2DOtp() {
        return String.valueOf(10 + new Random().nextInt(90));
    }

    private static String generate3DOtp() {
        return String.valueOf(100 + new Random().nextInt(99));
    }

    public boolean verifyOtp(Long chatId, String otp) {

        Optional<OtpCode> otpRecord = otpRepository.findValidOtp(chatId, otp);

        if (otpRecord.isPresent()) {

            OtpCode otpCode = otpRecord.get();
            otpCode.setUsed(true);
            otpRepository.save(otpCode);
            return true;
        }

        return false;
    }
}
