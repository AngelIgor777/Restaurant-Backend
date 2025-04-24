package org.test.restaurant_service.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalOrders {
    private List<ConcreteOrderId> totalPendingOrdersId;
    private List<ConcreteOrderId> totalCompletedOrdersId;
    private List<ConcreteOrderId> totalConfirmedOrdersId;
}
