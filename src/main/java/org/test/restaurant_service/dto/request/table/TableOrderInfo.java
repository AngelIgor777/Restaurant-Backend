package org.test.restaurant_service.dto.request.table;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class TableOrderInfo {
    private int tableId;
    private Set<Integer> ordersId;
}
