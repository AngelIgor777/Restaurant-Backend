package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.ProductTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTranslationResponseDTO;
import org.test.restaurant_service.entity.ProductTranslation;

import java.util.List;

public interface ProductTranslationService {
    ProductTranslationResponseDTO getTranslation(Integer productId, String languageCode);

    ProductTranslation getTranslationByProductName(String translationName);
    ProductTranslation getTranslationByProductId(Integer productId);

    List<ProductTranslationResponseDTO> getAllTranslations(String languageCode);

    ProductTranslationResponseDTO createOrUpdateTranslation(ProductTranslationRequestDTO requestDTO);

    void deleteTranslation(Integer id);
}
