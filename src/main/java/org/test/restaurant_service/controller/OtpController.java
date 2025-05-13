package org.test.restaurant_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.AuthenticationService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.service.impl.OtpService;

import java.time.Duration;


@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {


    private final OtpService otpService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/generate")
    public void generateOtp(@RequestParam Long chatId) {
        User user = userService.findByChatId(chatId);

        otpService.generateAndSendOtp(user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam Long chatId, @RequestParam String otp) {
        JwtResponse jwt = authenticationService.authenticate(chatId, otp);
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", jwt.getAccessToken())
                .httpOnly(true)          // защищает от XSS
                .secure(false)            // отправлять только по HTTPS
                .sameSite("Strict")      // защита от CSRF
                .path("/")               // ко всем эндпоинтам
                .maxAge(Duration.ofMinutes(60)) // TTL
                .build();


        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString()).build();
    }
}
