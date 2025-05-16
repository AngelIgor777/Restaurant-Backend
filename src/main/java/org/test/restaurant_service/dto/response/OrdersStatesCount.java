package org.test.restaurant_service.dto.response;

import lombok.Data;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;
import org.test.restaurant_service.dto.response.order.OrderId;
import org.test.restaurant_service.dto.response.order.TotalOrders;

import java.util.List;

@Data
public class OrdersStatesCount {
    private TotalOrders totalOrders;
    private List<TableOrderInfo> tablesOrderInfo;
    private List<Integer> ordersForDelete;
}
