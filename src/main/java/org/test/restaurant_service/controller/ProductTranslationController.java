package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ProductTranslationResponseDTO> getTranslation(
            @PathVariable Integer productId, @RequestParam String lang) {
        Optional<ProductTranslationResponseDTO> translation = translationService.getTranslation(productId, lang);
        return translation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductTranslationResponseDTO>> getAllTranslations(@RequestParam(defaultValue = "en") String lang) {
        List<ProductTranslationResponseDTO> translations = translationService.getAllTranslations(lang);
        return ResponseEntity.ok(translations);
    }

    @PostMapping
    public ResponseEntity<ProductTranslationResponseDTO> createOrUpdateTranslation(
            @Valid @RequestBody ProductTranslationRequestDTO requestDTO) {
        ProductTranslationResponseDTO savedTranslation = translationService.createOrUpdateTranslation(requestDTO);
        return ResponseEntity.ok(savedTranslation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Integer id) {
        translationService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }
}
