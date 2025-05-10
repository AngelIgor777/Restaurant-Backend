package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.response.Codes;
import org.test.restaurant_service.service.impl.cache.CodeCacheService;

@Service
@RequiredArgsConstructor
public class CodeService {
    private final CodeCacheService codeCacheService;

    private final WebSocketSender webSocketSender;


    @Scheduled(fixedRate = 5 * 60_000)
    public void rotateCodes() {
        Codes codes = codeCacheService.rotateCodes();
        webSocketSender.sendCode(codes.getTrueCode());
    }
}
