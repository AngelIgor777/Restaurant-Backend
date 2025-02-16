package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.exception.InvalidOtpException;
import org.test.restaurant_service.service.AuthenticationService;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.service.OtpService;
import org.test.restaurant_service.service.UserService;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final OtpService otpService;
    private final JwtService jwtService;

    @Override
    public JwtResponse authenticate(Long chatId, String otp) {
        boolean isVerified = otpService.verifyOtp(chatId, otp);

        if (!isVerified) {
            throw new InvalidOtpException("OTP verification failed for chatId: " + chatId);
        }

        User user = userService.findByChatId(chatId);

        String accessToken = jwtService.generateUserAccessToken(chatId, user.getRoles()
                .stream()
                .map(role -> role.getRoleName().name())
                .toList());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .userUUID(user.getUuid())
                .build();
    }

}
