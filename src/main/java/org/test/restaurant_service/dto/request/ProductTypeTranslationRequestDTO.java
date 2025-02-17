package org.test.restaurant_service.dto.request;

import lombok.*;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTypeTranslationRequestDTO {

    @NotNull
    private Integer productTypeId;

    @NotBlank
    @Size(max = 10)
    private String languageCode;

    @NotBlank
    @Size(max = 128)
    private String name;
}
