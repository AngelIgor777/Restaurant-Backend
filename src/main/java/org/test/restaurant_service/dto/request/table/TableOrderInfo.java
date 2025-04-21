package org.test.restaurant_service.dto.request.table;

import lombok.Data;
import org.test.restaurant_service.dto.response.TableStateOrders;


@Data
public class TableOrderInfo {
    private int tableId;
    private TableStateOrders pendingOrders;
    private TableStateOrders completedOrders;
    private TableStateOrders confirmedOrders;
}
