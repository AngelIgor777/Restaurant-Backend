package org.test.restaurant_service.dto.request;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @NotNull
    private Integer typeId;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;
}
