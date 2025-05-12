package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.feats.Features;
import org.test.restaurant_service.dto.response.FeatureStatusResponseDTO;
import org.test.restaurant_service.service.impl.FeatureService;
import org.test.restaurant_service.telegram.handling.TelegramBot;

import java.util.List;

@RestController
@RequestMapping("/api/v1/features")
@RequiredArgsConstructor
@PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
public class FeatureController {
    private final FeatureService featureService;
    private final TelegramBot telegramBot;

    /**
     * Включить фичу навсегда.
     */
    @PostMapping("/{feature}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<Void> enableFeature(@PathVariable Features feature) {
        featureService.enableFeature(feature);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{feature}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<Void> disableFeature(@PathVariable Features feature) {
        featureService.disableFeature(feature);
        if (feature.equals(Features.WAITER_CALL)) {
            telegramBot.updateCommands();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{feature}")
    public ResponseEntity<FeatureStatusResponseDTO> getFeature(
            @PathVariable Features feature) {
        FeatureStatusResponseDTO dto = featureService.getFeatureStatus(feature);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<FeatureStatusResponseDTO>> getAllFeatures() {
        List<FeatureStatusResponseDTO> list = featureService.getAllFeaturesStatus();
        return ResponseEntity.ok(list);
    }
}
