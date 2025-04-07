package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.service.PrinterService;


@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class PrintOrderController {

    private final PrinterService printerService;

    @PostMapping("/{orderId}/print")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public void sendToPrinter(@PathVariable Integer orderId) {
        printerService.sendOrderToPrinter(orderId);
    }
}
