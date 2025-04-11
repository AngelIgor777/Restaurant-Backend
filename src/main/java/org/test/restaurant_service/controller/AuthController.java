package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.admin.AdminDataRequest;
import org.test.restaurant_service.dto.request.admin.PasswordChangeRequest;
import org.test.restaurant_service.dto.response.admin.JwtResponse;
import org.test.restaurant_service.service.impl.AuthService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<JwtResponse> login(@RequestBody AdminDataRequest request) {
        JwtResponse login = authService.login(request);
        return ResponseEntity.ok(login);
    }

    @PostMapping("/register")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<JwtResponse> register(@RequestBody AdminDataRequest admin, @RequestParam UUID userUUID) {
        JwtResponse register = authService.register(admin, userUUID);
        return ResponseEntity.ok(register);
    }

    @GetMapping("/isRegistered")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<Boolean> register(@RequestParam UUID userUUID) {
        boolean isRegisteredUser = authService.isRegistered(userUUID);
        return ResponseEntity.ok(isRegisteredUser);
    }

    @PostMapping("/change-password/{userUUID}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<String> changePassword(
            @PathVariable UUID userUUID,
            @RequestBody PasswordChangeRequest request
    ) {
        authService.changePassword(userUUID, request);
        return ResponseEntity.ok("Password changed successfully");
    }
}