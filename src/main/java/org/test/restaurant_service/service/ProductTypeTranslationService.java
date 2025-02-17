package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;

import java.util.Optional;

public interface ProductTypeTranslationService {
    Optional<ProductTypeTranslationResponseDTO> getTranslation(Integer productTypeId, String languageCode);

    ProductTypeTranslationResponseDTO createTranslation(ProductTypeTranslationRequestDTO requestDTO);

    ProductTypeTranslationResponseDTO updateTranslation(Integer id, ProductTypeTranslationRequestDTO requestDTO);

    void deleteTranslation(Integer id);
}
