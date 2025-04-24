package org.test.restaurant_service.service.impl.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.order.ConcreteOrderId;
import org.test.restaurant_service.dto.response.order.TotalOrders;
import org.test.restaurant_service.dto.response.order.OrderId;
import org.test.restaurant_service.entity.Order;

@Service
@RequiredArgsConstructor
public class TotalOrdersCacheService {

    private static final String KEY = "totalOrders";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void setTotalOrders(TotalOrders orders) {
        redisTemplate.opsForValue().set(KEY, orders);
    }

    public TotalOrders getTotalOrders() {
        Object raw = redisTemplate.opsForValue().get(KEY);
        if (raw == null) {
            return new TotalOrders();
        }
        return objectMapper.convertValue(raw, TotalOrders.class);
    }

    public TotalOrders addPendingOrder(OrderId orderId) {
        TotalOrders orders = getTotalOrders();
        orders.getTotalPendingOrdersId().add(new ConcreteOrderId(orderId.getId()));
        setTotalOrders(orders);
        return orders;
    }

    public TotalOrders addConfirmedOrder(OrderId orderId) {
        TotalOrders orders = getTotalOrders();
        orders.getTotalConfirmedOrdersId().add(new ConcreteOrderId(orderId.getId()));
        setTotalOrders(orders);
        return orders;

    }

    public TotalOrders addCompletedOrder(OrderId orderId) {
        TotalOrders orders = getTotalOrders();
        orders.getTotalCompletedOrdersId().add(new ConcreteOrderId(orderId.getId()));
        setTotalOrders(orders);
        return orders;
    }


    public TotalOrders updateOrderStatus(OrderId orderId,
                                         Order.OrderStatus fromStatus,
                                         Order.OrderStatus toStatus) {
        TotalOrders orders = getTotalOrders();
        removeFromList(orderId, orders, fromStatus);
        addToList(orderId, orders, toStatus);
        setTotalOrders(orders);

        return orders;
    }

    private void removeFromList(OrderId orderId, TotalOrders orders, Order.OrderStatus status) {
        switch (status) {
            case PENDING:
                orders.getTotalPendingOrdersId().remove(orderId);
                break;
            case CONFIRMED:
                orders.getTotalConfirmedOrdersId().remove(orderId);
                break;
            case COMPLETED:
                orders.getTotalCompletedOrdersId().remove(orderId);
                break;
        }
    }

    private void addToList(OrderId orderId, TotalOrders orders, Order.OrderStatus status) {
        switch (status) {
            case PENDING:
                orders.getTotalPendingOrdersId().add(new ConcreteOrderId(orderId.getId()));
                break;
            case CONFIRMED:
                orders.getTotalConfirmedOrdersId().add(new ConcreteOrderId(orderId.getId()));
                break;
            case COMPLETED:
                orders.getTotalCompletedOrdersId().add(new ConcreteOrderId(orderId.getId()));
                break;
        }
    }


    public void clearAll() {
        redisTemplate.delete(KEY);
    }
}
