package org.test.restaurant_service.dto.request.table;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TableOrdersPriceInfo {
    private BigDecimal price;
}
