package org.test.restaurant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResponseDTO {

    private Integer id;

    private String code;

    private String description;

    private BigDecimal discount;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;
}
