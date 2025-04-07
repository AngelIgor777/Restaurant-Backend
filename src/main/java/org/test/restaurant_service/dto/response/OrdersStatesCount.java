package org.test.restaurant_service.dto.response;

import lombok.Data;

@Data
public class OrdersStatesCount {
    private int pendingOrders;
    private int completedOrders;
    private int confirmedOrders;
}
