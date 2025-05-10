package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.WaiterCallRequestDTO;
import org.test.restaurant_service.service.impl.cache.WaiterCallCacheService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/waiter-calls")
@RequiredArgsConstructor
public class WaiterCallController {

    private final WaiterCallCacheService waiterCallCacheService;


    @GetMapping("/{tableNumber}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<WaiterCallRequestDTO> getCallByTable(@PathVariable Integer tableNumber) {
        WaiterCallRequestDTO call = waiterCallCacheService.getWaiterCallByTable(tableNumber);
        if (call == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(call);
    }

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<List<WaiterCallRequestDTO>> getAllWaiterCalls() {
        List<WaiterCallRequestDTO> calls = waiterCallCacheService.getAllWaiterCalls();
        return ResponseEntity.ok(calls);
    }

    @DeleteMapping("/{tableNumber}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<Void> deleteCall(@PathVariable Integer tableNumber) {
        waiterCallCacheService.deleteWaiterCall(tableNumber);
        return ResponseEntity.noContent().build();
    }
}
