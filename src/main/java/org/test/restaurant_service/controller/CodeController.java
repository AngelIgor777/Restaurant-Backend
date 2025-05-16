package org.test.restaurant_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.service.impl.CodeService;
import org.test.restaurant_service.service.impl.cache.CodeCacheService;

@RestController
@RequestMapping("/api/v1/code")
public class CodeController {

    private final CodeCacheService codeCacheService;
    private final CodeService codeService;

    public CodeController(CodeCacheService codeCacheService, CodeService codeService) {
        this.codeCacheService = codeCacheService;
        this.codeService = codeService;
    }

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<Integer> getValidCode() {
        int validCode = codeCacheService.getActivationCodes().getTrueCode();
        return ResponseEntity.ok(validCode);
    }

    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public void rotateCodes() {
        codeService.rotateCodes();
    }
}
