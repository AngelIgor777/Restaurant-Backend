package org.test.restaurant_service.controller;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.service.JwtService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jwt")
@RequiredArgsConstructor
public class JwtController {
    private static final Logger log = LoggerFactory.getLogger(JwtController.class);
    private final JwtService jwtService;

    @PostMapping
    public JwtResponse generateJwtForUser(@RequestParam UUID userUUID) {
        log.debug("Received request to generate jwt for user {}", userUUID);
        JwtResponse response = jwtService.generateUserAccessToken(UUID.fromString(userUUID.toString()));
        log.debug("Generated jwt for user {}", response);
        return response;
    }
}
