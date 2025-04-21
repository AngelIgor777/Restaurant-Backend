package org.test.restaurant_service.dto.request.table;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TableOrdersPriceInfo {
    private BigDecimal price;
    private UUID sessionUUID;
}
