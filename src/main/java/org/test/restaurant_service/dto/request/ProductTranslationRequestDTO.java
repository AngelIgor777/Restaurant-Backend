package org.test.restaurant_service.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class ProductTranslationRequestDTO {
    @NotNull
    private Integer productId;

    @NotBlank
    @Size(max = 5)
    private String languageCode;

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;
}
