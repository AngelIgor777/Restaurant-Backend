package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.OrderService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final OrderService orderService;

    @Scheduled(cron = "0 0 */2 * * *")
    public void deleteAllOrdersWithStatusPendingAtLastHours() {
        orderService.deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus.PENDING, LocalDateTime.now().minusHours(2), LocalDateTime.now());
    }
}
