package org.test.restaurant_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
