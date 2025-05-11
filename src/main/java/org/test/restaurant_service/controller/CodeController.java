package org.test.restaurant_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.service.impl.cache.CodeCacheService;

@RestController
@RequestMapping("/api/v1/code")
public class CodeController {

    private final CodeCacheService codeCacheService;

    public CodeController(CodeCacheService codeCacheService) {
        this.codeCacheService = codeCacheService;
    }
    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<Integer> getValidCode() {
        int validCode = codeCacheService.getOrderCode().getTrueCode();
        return ResponseEntity.ok(validCode);
    }
}
