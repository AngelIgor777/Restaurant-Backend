package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.impl.ExportService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/exportOrders")
    public ResponseEntity<byte[]> exportProducts(
            @RequestParam Order.OrderStatus status,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime to) {

        return exportService.exportOrdersToExcel(status, from, to);
    }
}


