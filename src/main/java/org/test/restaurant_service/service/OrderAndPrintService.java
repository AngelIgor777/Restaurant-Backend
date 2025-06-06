package org.test.restaurant_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.request.order.ProductsForPrintRequest;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;
import org.test.restaurant_service.dto.response.OrdersStatesCount;
import org.test.restaurant_service.dto.response.order.TotalOrders;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.impl.TableOrderScoreService;
import org.test.restaurant_service.service.impl.cache.TableCacheService;
import org.test.restaurant_service.service.impl.cache.TotalOrdersCacheService;

import javax.print.PrintService;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderAndPrintService {

    private final OrderService orderService;
    private final PrinterService printerService;
    private final TableOrderScoreService tableOrderScoreService;
    private final TableCacheService tableCacheService;
    private final TotalOrdersCacheService totalOrdersCacheService;
    private final WebSocketSender webSocketSender;

    @Transactional
    public void confirmOrder(Integer orderId, UUID sessionUUID, Order.OrderStatus from, ProductsForPrintRequest productsForPrintRequest) {
        Order orderById = orderService.getOrderById(orderId);
        orderById.setStatus(Order.OrderStatus.CONFIRMED);
        orderById.setOtp(null);

        //queue is important
        if (from.equals(Order.OrderStatus.PENDING)) {
            printerService.sendOrderToPrinter(null, productsForPrintRequest, orderById);
        }

        TableOrderInfo tableOrderInfo = null;
        if (sessionUUID != null) {
            tableOrderScoreService.save(orderById.getTable(), orderById, sessionUUID);
            tableOrderInfo = tableCacheService.changeOrderStateForTable(orderId, orderById.getTable().getId(), from, Order.OrderStatus.CONFIRMED);
        }

        TotalOrders totalOrders = totalOrdersCacheService.updateOrderStatus(() -> orderId, from, Order.OrderStatus.CONFIRMED);
        OrdersStatesCount ordersStatesCount = new OrdersStatesCount();
        ordersStatesCount.setTotalOrders(totalOrders);
        if (tableOrderInfo != null) {
            ordersStatesCount.setTablesOrderInfo(List.of(tableOrderInfo));
        }

        webSocketSender.sendOrdersStateCount(ordersStatesCount);
    }
}
