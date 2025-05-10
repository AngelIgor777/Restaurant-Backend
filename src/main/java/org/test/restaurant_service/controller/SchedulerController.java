package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.CronLocalTimeResponseDto;
import org.test.restaurant_service.telegram.util.scheduling.ScheduledTask;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final ScheduledTask scheduledTask;

    @PostMapping("/update-cron")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public void updateCron(@RequestParam String cronExpression) {
        scheduledTask.updateCronExpression(cronExpression);

    }

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public CronLocalTimeResponseDto getCron() {
        String cronExpression = scheduledTask.getCronExpression();
        CronLocalTimeResponseDto cronLocalTimeResponseDto = new CronLocalTimeResponseDto();
        cronLocalTimeResponseDto.setCron(cronExpression);
        cronLocalTimeResponseDto.setLocalTime(LocalTime.now());
        return cronLocalTimeResponseDto;
    }

    @PostMapping("/start")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public String startScheduler() {
        scheduledTask.startTask();
        return "Scheduler started.";
    }

    @PostMapping("/stop")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public String stopScheduler() {
        scheduledTask.stopTask();
        return "Scheduler stopped.";
    }
}
