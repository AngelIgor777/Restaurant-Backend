package org.test.restaurant_service.dto.response;

public record ProductI18nResponseDTO(
        Long    id,
        Long    productId,
        Integer langId,
        String  name,
        String  description) {}

