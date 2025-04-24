package org.test.restaurant_service.dto.request.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.test.restaurant_service.dto.response.TableStateOrders;

import java.util.List;


@Data
@AllArgsConstructor
public class TableOrderInfo {
    private int tableId;
    private List<Integer> pendingOrders;
    private List<Integer> completedOrders;
    private List<Integer> confirmedOrders;
}
