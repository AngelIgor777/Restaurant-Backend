package org.test.restaurant_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketController;
import org.test.restaurant_service.dto.response.OrderForPrintDto;
import org.test.restaurant_service.dto.response.ProductItem;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrinterService {
    private static final Logger log = LoggerFactory.getLogger(PrinterService.class);
    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final WebSocketController webSocketController;

    public void sendOrderToPrinter(Integer orderId) {
        Order order = orderService.getOrderById(orderId);
        OrderForPrintDto orderForPrintDto = new OrderForPrintDto();
        orderForPrintDto.setCreatedAt(order.getCreatedAt());
        orderForPrintDto.setTotalPrice(order.getTotalPrice());
        orderForPrintDto.setPaymentMethod(order.getPaymentMethod().name());
        if (order.isOrderInRestaurant()) {
            orderForPrintDto.setTable(order.getTable().getNumber());
        } else if (order.isOrderOutRestaurant()) {
            orderForPrintDto.setPhoneNumber(order.getPhoneNumber());
        }
        List<ProductItem> productItems = orderProductService.getOrderProductsByOrderId(orderId)
                .stream()
                .map(orderProduct -> {
                    Product product = orderProduct.getProduct();
                    String productTypeName = product.getType().getName();
                    String productName = product.getName();
                    Integer quantity = orderProduct.getQuantity();
                    BigDecimal price = product.getPrice().multiply(new BigDecimal(quantity));
                    return new ProductItem(productTypeName, productName, quantity, price);
                }).toList();
        orderForPrintDto.setProductItemList(productItems);
        webSocketController.sendOrderForLocalPrinter(orderForPrintDto);
        log.debug("Send order for printing: {}", orderForPrintDto);
    }

}
