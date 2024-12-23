package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.service.impl.OtpServiceImpl;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpServiceImpl otpService;

    @PostMapping("/verify")
    public ResponseEntity<JwtResponse> verifyOtp(@RequestParam("otpCode") String otpCode) {
        JwtResponse response = otpService.verifyOtpCode(otpCode);

        return ResponseEntity.ok().body(response);
    }
}
