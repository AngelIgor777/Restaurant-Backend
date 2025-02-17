package org.test.restaurant_service.dto.response;

import lombok.Data;

@Data
public class ProductTranslationResponseDTO {
    private Integer id;
    private Integer productId;
    private String languageCode;
    private String name;
    private String description;
}

