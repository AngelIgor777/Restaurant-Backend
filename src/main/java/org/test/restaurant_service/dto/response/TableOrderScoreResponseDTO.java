package org.test.restaurant_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TableOrderScoreResponseDTO {
    private int tableId;
    List<OrderProductResponseWithPayloadDto> orders;
    private BigDecimal totalPrice;
}
