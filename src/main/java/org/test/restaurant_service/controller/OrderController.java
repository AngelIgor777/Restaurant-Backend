package org.test.restaurant_service.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.service.OrderService;

import javax.ws.rs.HttpMethod;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("/complete/{orderId}")
    public void completeOrder(@PathVariable Integer orderId) {
        orderService.completeOrder(orderId);
    }

    @PostMapping("/confirm/{orderId}")
    public void confirmOrder(@PathVariable Integer orderId) {
        orderService.confirmOrder(orderId);
    }

    @GetMapping("/user")
    public List<OrderProductResponseWithPayloadDto> getUserOrders(@RequestParam UUID userUUID, Pageable pageable) {

        return orderService.getAllUserOrdersProductResponseWithPayloadDto(userUUID, pageable);
    }
}
