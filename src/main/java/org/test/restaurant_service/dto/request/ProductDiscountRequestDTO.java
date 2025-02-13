package org.test.restaurant_service.dto.request;

import lombok.Data;
import org.test.restaurant_service.entity.Product;


import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDiscountRequestDTO {


    @NotBlank(message = "Code cannot be blank")
    @Size(max = 64, message = "Code must not exceed 64 characters")
    private String code;

    @NotNull
    private Integer productId;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount must be greater than 0")
    private BigDecimal discount;

    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;

    @NotNull(message = "Valid to date is required")
    private LocalDateTime validTo;
}
