package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.service.ProductTypeTranslationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/product-type-translations")
@RequiredArgsConstructor
@Validated
public class ProductTypeTranslationController {

    private final ProductTypeTranslationService productTypeTranslationService;

    @GetMapping("/{productTypeId}")
    public ResponseEntity<ProductTypeTranslationResponseDTO> getProductTypeTranslation(
            @PathVariable Integer productTypeId,
            @RequestParam String lang) {

        return productTypeTranslationService.getTranslation(productTypeId, lang)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductTypeTranslationResponseDTO> createTranslation(
            @Valid @RequestBody ProductTypeTranslationRequestDTO requestDTO) {
        ProductTypeTranslationResponseDTO createdTranslation = productTypeTranslationService.createTranslation(requestDTO);
        return ResponseEntity.status(201).body(createdTranslation);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductTypeTranslationResponseDTO> updateTranslation(
            @PathVariable Integer id,
            @Valid @RequestBody ProductTypeTranslationRequestDTO requestDTO) {
        ProductTypeTranslationResponseDTO updatedTranslation = productTypeTranslationService.updateTranslation(id, requestDTO);
        return ResponseEntity.ok(updatedTranslation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Integer id) {
        productTypeTranslationService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }
}
