//package org.test.restaurant_service.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.test.restaurant_service.telegram.util.scheduling.ScheduledTask;
//
//@RestController
//@RequestMapping("/api/v1/scheduler")
//public class SchedulerController {
//
//    private final ScheduledTask scheduledTask;
//
//    @Autowired
//    public SchedulerController(ScheduledTask scheduledTask) {
//        this.scheduledTask = scheduledTask;
//    }
//
//    @PostMapping("/update-cron")
//    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
//    public String updateCron(@RequestParam String cronExpression) {
//        try {
//            scheduledTask.updateCronExpression(cronExpression);
//            return "Scheduler cron updated to: " + cronExpression;
//        } catch (Exception e) {
//            return "Failed to update cron: " + e.getMessage();
//        }
//    }
//
//    @PostMapping("/start")
//    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
//    public String startScheduler() {
//        scheduledTask.startTask();
//        return "Scheduler started.";
//    }
//
//    @PostMapping("/stop")
//    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
//    public String stopScheduler() {
//        scheduledTask.stopTask();
//        return "Scheduler stopped.";
//    }
//}
