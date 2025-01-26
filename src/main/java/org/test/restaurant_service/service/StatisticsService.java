package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.response.StatisticsResultResponseDto;

import java.time.LocalDateTime;

public interface StatisticsService {


    StatisticsResultResponseDto getStatistics(LocalDateTime from, LocalDateTime to);

}
