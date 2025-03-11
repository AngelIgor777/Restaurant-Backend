package org.test.restaurant_service.dto.response;

import lombok.Data;

import java.time.LocalTime;

@Data
public class CronLocalTimeResponseDto {
    private LocalTime localTime;
    private String cron;
}
