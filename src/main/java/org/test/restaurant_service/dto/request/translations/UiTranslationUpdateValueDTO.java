package org.test.restaurant_service.dto.request.translations;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UiTranslationUpdateValueDTO {
    @NotBlank
    private String key;

    @NotBlank
    private String value;

    @NotNull
    private Integer langId;
}
