package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.OrderService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private final OrderService orderService;

    @Scheduled(cron = "0 0 */2 * * *")
    public void deleteAllOrdersWithStatusPendingAtLastHours() {
        log.info("Start deleting orders");
        orderService.deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus.PENDING, LocalDateTime.now().minusHours(2), LocalDateTime.now());
    }
}
