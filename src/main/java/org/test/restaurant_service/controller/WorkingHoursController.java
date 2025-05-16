package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.entity.WorkingHours;
import org.test.restaurant_service.service.impl.WorkingHoursService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/working-hours")
@RequiredArgsConstructor
public class WorkingHoursController {

    private final WorkingHoursService service;

    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<WorkingHours> create(@RequestBody WorkingHours hours) {
        return ResponseEntity.ok(service.save(hours));
    }

    @PutMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<WorkingHours> update(@RequestBody WorkingHours hours) {
        return ResponseEntity.ok(service.update(hours));
    }

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<List<WorkingHours>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{dayOfWeek}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<WorkingHours> getByDay(@PathVariable short dayOfWeek) {
        return ResponseEntity.ok(service.getByDayOfWeek(dayOfWeek));
    }
}
