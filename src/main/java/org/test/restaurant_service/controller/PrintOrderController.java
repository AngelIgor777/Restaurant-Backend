package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.order.ProductsForPrintRequest;
import org.test.restaurant_service.service.PrinterService;


@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class PrintOrderController {

    private final PrinterService printerService;

    @PostMapping("/{orderId}/print")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public void sendToPrinter(@PathVariable Integer orderId,
                              @RequestBody ProductsForPrintRequest productsId) {
        printerService.sendOrderToPrinter(orderId, productsId, null);
    }
}
