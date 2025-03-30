package org.test.restaurant_service.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.service.OrderService;

import java.util.Collections;
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
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<List<OrderProductResponseWithPayloadDto>> getAllOrders() {
        List<OrderProductResponseWithPayloadDto> orders = orderService.getAllOrdersProductResponseWithPayloadDto();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProductResponseWithPayloadDto> getOrder(@PathVariable Integer id) {
        OrderProductResponseWithPayloadDto order = orderService.getOrderProductResponseWithPayloadDto(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/search")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<OrderProductResponseWithPayloadDto> searchOrderByValidationCode(@RequestParam String query) {
        OrderProductResponseWithPayloadDto order = orderService.searchOrderProductResponseWithPayloadDtoByValidationCode(query);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/complete/{orderId}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public void completeOrder(@PathVariable Integer orderId) {
        orderService.completeOrder(orderId);
    }

    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    @PostMapping("/confirm/{orderId}")
    public void confirmOrder(@PathVariable Integer orderId) {
        orderService.confirmOrder(orderId);
    }

    @GetMapping("/user")
    public List<OrderProductResponseWithPayloadDto> getUserOrders(@RequestParam UUID userUUID, Pageable pageable) {
        List<OrderProductResponseWithPayloadDto> allUserOrdersProductResponseWithPayloadDto = orderService.getAllUserOrdersProductResponseWithPayloadDto(userUUID, pageable);

        return allUserOrdersProductResponseWithPayloadDto;
    }

    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        orderService.delete(id);
    }
}
