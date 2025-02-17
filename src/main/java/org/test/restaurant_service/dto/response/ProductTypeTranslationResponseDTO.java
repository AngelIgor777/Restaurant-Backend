package org.test.restaurant_service.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeTranslationResponseDTO {
    private Integer id;
    private Integer productTypeId;
    private String languageCode;
    private String name;
}
