package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.ProductTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTranslationResponseDTO;
import org.test.restaurant_service.service.ProductTranslationService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/product-translations")
@RequiredArgsConstructor
public class ProductTranslationController {

    private final ProductTranslationService translationService;

    @GetMapping("/{productId}")
    public ProductTranslationResponseDTO getTranslation(
            @PathVariable Integer productId, @RequestParam String lang) {
        ProductTranslationResponseDTO translation = translationService.getTranslation(productId, lang);
        return translation;
    }

    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<ProductTranslationResponseDTO> createOrUpdateTranslation(
            @Valid @RequestBody ProductTranslationRequestDTO requestDTO) {
        ProductTranslationResponseDTO savedTranslation = translationService.createOrUpdateTranslation(requestDTO);
        return ResponseEntity.ok(savedTranslation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Integer id) {
        translationService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }
}
