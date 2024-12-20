package org.test.restaurant_service.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class OrderRequestDTO {
    @NotNull
    private Integer tableId;

    @NotNull
    private String status;

    private String paymentMethod;

    @Positive
    private BigDecimal totalPrice;
}
