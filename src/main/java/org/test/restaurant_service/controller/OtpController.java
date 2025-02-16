package org.test.restaurant_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.AuthenticationService;
import org.test.restaurant_service.service.OtpService;
import org.test.restaurant_service.service.UserService;


@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {


    private final OtpService otpService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PreAuthorize("@securityService.userIsAdminOrModerator(#chatId)")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/generate")
    public void generateOtp(@RequestParam Long chatId) {
        User user = userService.findByChatId(chatId);

        otpService.generateAndSendOtp(user);
    }

    @PreAuthorize("@securityService.userIsAdminOrModerator(#chatId)")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/verify")
    public JwtResponse verifyOtp(@RequestParam Long chatId, @RequestParam String otp) {
        return authenticationService.authenticate(chatId, otp);
    }
}
