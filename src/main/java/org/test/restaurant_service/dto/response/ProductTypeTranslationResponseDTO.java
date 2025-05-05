package org.test.restaurant_service.dto.response;

public record ProductTypeTranslationResponseDTO(
        Long    id,
        Long    productTypeId,
        Integer langId,
        String  name) {}