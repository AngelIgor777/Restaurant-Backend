package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslResponseDTO;
import org.test.restaurant_service.entity.ProductTypeTransl;

public interface ProductTypeTranslationService {
    ProductTypeTranslResponseDTO getTranslation(Integer productTypeId, String languageCode);

    ProductTypeTransl getTranslation(String productTypeName, String languageCode);

    ProductTypeTranslResponseDTO createTranslation(ProductTypeTranslationRequestDTO requestDTO);

    ProductTypeTranslResponseDTO updateTranslation(Integer id, ProductTypeTranslationRequestDTO requestDTO);

    void deleteTranslation(Integer id);
    ProductTypeTransl getByRoTranslation(String productTypeName, String languageCode);
}
