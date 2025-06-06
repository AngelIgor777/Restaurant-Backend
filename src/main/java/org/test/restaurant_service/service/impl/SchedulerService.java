package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.response.OrdersStatesCount;
import org.test.restaurant_service.dto.response.order.TotalOrders;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.OrderService;
import org.test.restaurant_service.service.impl.cache.TableCacheService;
import org.test.restaurant_service.service.impl.cache.TotalOrdersCacheService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private final OrderService orderService;
    private final OrderTableScoreService orderTableScoreService;
    private final WebSocketSender webSocketSender;
    private final TableCacheService tableCacheService;
    private final TotalOrdersCacheService totalOrdersCacheService;

    @Scheduled(cron = "0 0 */2 * * *")
    public void deleteAllOrdersWithStatusPendingAtLastHours() {
        log.info("Start deleting orders");
        List<Integer> idsForDeleting = orderService.deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus.PENDING, LocalDateTime.now().minusHours(2), LocalDateTime.now());

        removeOrdersFromCache(idsForDeleting, Order.OrderStatus.PENDING);


        OrdersStatesCount ordersStatesCount = new OrdersStatesCount();
        ordersStatesCount.setOrdersForDelete(idsForDeleting);
        webSocketSender.sendOrdersStateCount(ordersStatesCount);
    }

    @Scheduled(cron = "0 0 4  * * *")
    public void deleteAllScoresEveryDay() {
        orderTableScoreService.deleteAll();
    }


    private void removeOrdersFromCache(List<Integer> orderIds, Order.OrderStatus status) {
        if (orderIds == null || orderIds.isEmpty()) {
            return;
        }

        TotalOrders totalOrders = totalOrdersCacheService.getTotalOrders();
        orderIds.forEach(id ->
                totalOrders.getTotalPendingOrdersId()
                        .removeIf(concrete -> concrete.getId().equals(id))
        );
        totalOrdersCacheService.setTotalOrders(totalOrders);

        tableCacheService.getAllTableOrderInfos().forEach(tableInfo -> {
            int tableId = tableInfo.getTableId();
            orderIds.forEach(id ->
                    tableCacheService.deleteOrderIdFromTable(tableId, id, status)
            );
        });
    }


}
