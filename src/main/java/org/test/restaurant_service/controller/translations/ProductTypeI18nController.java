package org.test.restaurant_service.controller.translations;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.translations.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.service.impl.ProductTypeI18nService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/product-types/translations")
@RequiredArgsConstructor
@Validated
public class ProductTypeI18nController {

    private final ProductTypeI18nService service;

    @PostMapping("/{typeId}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<ProductTypeTranslationResponseDTO> create(@PathVariable Integer typeId,
                                                                    @Valid @RequestBody ProductTypeTranslationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(typeId, dto));
    }


    @PatchMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ProductTypeTranslationResponseDTO update(@PathVariable Integer id,
                                                    @Valid @RequestBody ProductTypeTranslationRequestDTO dto) {
        return service.update(id, dto);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
