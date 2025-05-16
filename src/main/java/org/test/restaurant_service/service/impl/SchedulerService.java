package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.response.OrdersStatesCount;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private final OrderService orderService;
    private final OrderTableScoreService orderTableScoreService;
    private final WebSocketSender webSocketSender;

    @Scheduled(cron = "0 0 */2 * * *")
    public void deleteAllOrdersWithStatusPendingAtLastHours() {
        log.info("Start deleting orders");
        List<Integer> idsForDeleting = orderService.deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus.PENDING, LocalDateTime.now().minusHours(2), LocalDateTime.now());
        OrdersStatesCount ordersStatesCount = new OrdersStatesCount();
        ordersStatesCount.setOrdersForDelete(idsForDeleting);
        webSocketSender.sendOrdersStateCount(ordersStatesCount);
    }

    @Scheduled(cron = "0 0 4  * * *")
    public void deleteAllScoresEveryDay() {
        orderTableScoreService.deleteAll();
    }
}
