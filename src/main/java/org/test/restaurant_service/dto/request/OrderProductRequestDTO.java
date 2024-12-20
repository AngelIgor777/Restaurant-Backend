package org.test.restaurant_service.dto.request;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class OrderProductRequestDTO {

    @NotNull
    private Integer productId;

    @Min(1)
    private Integer quantity;
}