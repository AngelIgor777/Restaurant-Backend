package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.entity.ProductTypeTranslation;

import java.util.Optional;

public interface ProductTypeTranslationService {
    ProductTypeTranslationResponseDTO getTranslation(Integer productTypeId, String languageCode);

    ProductTypeTranslation getTranslation(String productTypeName, String languageCode);

    ProductTypeTranslationResponseDTO createTranslation(ProductTypeTranslationRequestDTO requestDTO);

    ProductTypeTranslationResponseDTO updateTranslation(Integer id, ProductTypeTranslationRequestDTO requestDTO);

    void deleteTranslation(Integer id);
    ProductTypeTranslation getByRoTranslation(String productTypeName, String languageCode);
}
