package org.test.restaurant_service.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import org.test.restaurant_service.entity.Otp;
import org.test.restaurant_service.repository.OtpRepository;
import org.test.restaurant_service.service.OtpService;
import java.security.SecureRandom;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final SecureRandom random = new SecureRandom();

    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public Otp generateAndSaveOtp(Integer chatId, User user) {
        String username = user.getUserName() != null ? user.getUserName() : "unknown";
        String firstname = user.getFirstName();

        int otpCode = 100000 + random.nextInt(900000);

        Otp otp = Otp.builder()
                .chatId(chatId)
                .otpCode(String.valueOf(otpCode))
                .username(username)
                .firstname(firstname)
                .verified(false)
                .build();

        return otpRepository.save(otp);
    }
}
