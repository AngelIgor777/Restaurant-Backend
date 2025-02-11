package org.test.restaurant_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderProductResponseWithPayloadDto>> getAllOrders() {
        List<OrderProductResponseWithPayloadDto> orders = orderService.getAllOrdersProductResponseWithPayloadDto();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProductResponseWithPayloadDto> getOrder(@PathVariable Integer id) {
        OrderProductResponseWithPayloadDto order = orderService.getOrderProductResponseWithPayloadDto(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/complete/{orderId}")
    public void completeOrder(@PathVariable Integer orderId) {
        orderService.completeOrder(orderId);
    }

    @GetMapping("/confirm/{orderId}")
    public void confirmOrder(@PathVariable Integer orderId) {
        orderService.confirmOrder(orderId);
    }
}
