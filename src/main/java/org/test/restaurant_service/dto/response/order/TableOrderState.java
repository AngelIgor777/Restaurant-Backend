package org.test.restaurant_service.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.test.restaurant_service.entity.Order;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TableOrderState {
    private int orderId;
    private Order.OrderStatus orderStatus;
}
