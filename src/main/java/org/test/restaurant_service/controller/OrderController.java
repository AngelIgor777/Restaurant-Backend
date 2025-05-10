package org.test.restaurant_service.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.table.TableOrdersPriceInfo;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.dto.response.OrdersStatesCount;
import org.test.restaurant_service.dto.response.TableOrderScoreResponseDTO;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.OrderService;
import org.test.restaurant_service.service.impl.OrderTableScoreService;
import org.test.restaurant_service.service.impl.TableOrderScoreService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderTableScoreService orderTableScoreService;

    public OrderController(OrderService orderService, OrderTableScoreService orderTableScoreService) {
        this.orderService = orderService;
        this.orderTableScoreService = orderTableScoreService;
    }

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<List<OrderProductResponseWithPayloadDto>> getAllPendingOrders(@RequestParam Order.OrderStatus status) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfWorkDay = today.atTime(LocalTime.of(0, 1));
        LocalDateTime endOfWorkDay = today.atTime(LocalTime.of(23, 59));
        List<OrderProductResponseWithPayloadDto> orders = orderService.getAllOrdersProductResponseWithPayloadDto(status, startOfWorkDay, endOfWorkDay);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/countStats")
    @Transactional(readOnly = true)
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<OrdersStatesCount> getOrdersStatesCount() {
        OrdersStatesCount orders = orderService.getOrdersStatesCount();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<OrderProductResponseWithPayloadDto> getOrder(@PathVariable Integer id) {
        OrderProductResponseWithPayloadDto order = orderService.getOrderProductResponseWithPayloadDto(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/search")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<OrderProductResponseWithPayloadDto> searchOrderByValidationCode(@RequestParam String query) {
        OrderProductResponseWithPayloadDto order = orderService.searchOrderProductResponseWithPayloadDtoByValidationCode(query);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/searchOrders")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<List<OrderProductResponseWithPayloadDto>> searchOrdersById(@RequestParam List<Integer> ids) {
        List<OrderProductResponseWithPayloadDto> order = orderService.searchOrdersWithPayloadDtoById(ids);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/complete/{orderId}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public void completeOrder(@PathVariable Integer orderId,
                              @RequestParam(required = false) Integer tableId) {
        orderService.completeOrder(orderId, tableId);
    }

    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    @PostMapping("/confirm/{orderId}")
    public void confirmOrder(@PathVariable Integer orderId,
                             @RequestParam(required = false) UUID sessionUUID,
                             @RequestParam Order.OrderStatus from) {
        orderService.confirmOrder(orderId, sessionUUID, from);
    }

    @GetMapping("/user")
    @PreAuthorize("@securityService.userIsOwnerOrModeratorOrAdmin(authentication, #userUUID)")
    public List<OrderProductResponseWithPayloadDto> getUserOrders(@RequestParam UUID userUUID, Pageable pageable) {
        return orderService.getAllUserOrdersProductResponseWithPayloadDto(userUUID, pageable);
    }

    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id,
                       @RequestParam Order.OrderStatus status,
                       @RequestParam(required = false) Integer tableId) {
        orderService.delete(id, tableId, status);
    }

    @PostMapping("/count/{tableId}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public TableOrdersPriceInfo countPriceForTable(@PathVariable Integer tableId) {
        return orderService.countPriceForTable(tableId);
    }

    @GetMapping("/by-session")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public TableOrderScoreResponseDTO getOrdersBySessionUUID(@RequestParam UUID sessionUUID) {
        return orderTableScoreService.getTableOrderScore(sessionUUID);
    }

}
