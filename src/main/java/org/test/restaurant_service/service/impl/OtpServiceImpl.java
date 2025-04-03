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
        String otp = generateOtp();

        while (orderService.existsByOtp(otp)) {
            otp = generateOtp();
        }
        return otp;
    }

    private static String generateOtp() {
        return String.valueOf(10 + new Random().nextInt(90));
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
