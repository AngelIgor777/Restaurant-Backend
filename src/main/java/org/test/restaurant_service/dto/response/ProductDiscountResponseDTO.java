package org.test.restaurant_service.dto.response;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDiscountResponseDTO {

    private Integer id;

    private String code;

    private Integer productId;

    private String description;

    private BigDecimal discount;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

}
