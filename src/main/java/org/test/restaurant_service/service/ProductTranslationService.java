package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.ProductTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTranslationResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ProductTranslationService {
    Optional<ProductTranslationResponseDTO> getTranslation(Integer productId, String languageCode);

    List<ProductTranslationResponseDTO> getAllTranslations(String languageCode);

    ProductTranslationResponseDTO createOrUpdateTranslation(ProductTranslationRequestDTO requestDTO);

    void deleteTranslation(Integer id);
}
