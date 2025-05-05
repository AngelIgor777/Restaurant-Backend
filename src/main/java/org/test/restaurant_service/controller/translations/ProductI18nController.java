package org.test.restaurant_service.controller.translations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.translations.ProductI18nRequestDTO;
import org.test.restaurant_service.dto.response.ProductI18nResponseDTO;
import org.test.restaurant_service.service.impl.ProductI18nService;

@RestController
@RequestMapping("/api/v1/product-translations")
@RequiredArgsConstructor
public class ProductI18nController {

    private final ProductI18nService translationService;


    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<ProductI18nResponseDTO> createTranslation(
            @RequestParam Integer productId, @RequestBody ProductI18nRequestDTO dto) {
        return ResponseEntity.ok(translationService.create(productId, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Integer id) {
        translationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
