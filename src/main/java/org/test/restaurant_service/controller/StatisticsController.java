package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.StatisticsResultResponseDto;
import org.test.restaurant_service.service.StatisticsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<StatisticsResultResponseDto> getStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        StatisticsResultResponseDto statistics = statisticsService.getStatistics(from, to);
        return ResponseEntity.ok(statistics);
    }
}
