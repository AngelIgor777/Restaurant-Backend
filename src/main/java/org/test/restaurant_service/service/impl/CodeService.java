package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.response.Codes;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.service.impl.cache.CodeCacheService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodeService {

    private final CodeCacheService codeCacheService;

    private final WebSocketSender webSocketSender;
    private final JwtService jwtService;

    @Scheduled(fixedRate = 60_000)
    public void rotateCodes() {

        Codes codes = codeCacheService.rotateCodes();
        webSocketSender.sendCode(codes.getTrueCode());
    }


    public JwtResponse activateUser(UUID userUUID, int activationCode) {
        int trueCode = codeCacheService.getActivationCodes().getTrueCode();
        if (trueCode == activationCode) {
            codeCacheService.activateUser(userUUID);
            return jwtService.generateUserActivationToken(userUUID);
        } else {
            throw new AccessDeniedException("Access denied");
        }
    }

}
