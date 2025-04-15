package org.test.restaurant_service.dto.response;

import lombok.Data;
import org.test.restaurant_service.dto.request.table.OpenTables;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;

import java.util.List;

@Data
public class OrdersStatesCount {
    private int pendingOrders;
    private int completedOrders;
    private int confirmedOrders;
    private OpenTables openTables;
    List<TableOrderInfo> tableOrderInfos;
}
