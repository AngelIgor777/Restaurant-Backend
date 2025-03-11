package org.test.restaurant_service.telegram.util.scheduling;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.telegram.handling.TelegramBot;
import org.test.restaurant_service.telegram.util.TextUtil;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component

public class ScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    private final TaskScheduler taskScheduler;
    private final TextUtil textUtil;
    private final TelegramBot telegramBot;
    private final UserService userService;
    private ScheduledFuture<?> future;

    @Value("${scheduler.enabled:true}")
    private boolean isSchedulerEnabled;

    @Value("${scheduler.cron}")
    @Getter
    private String cronExpression;

    public ScheduledTask(TaskScheduler taskScheduler, TextUtil textUtil, TelegramBot telegramBot, UserService userService) {
        this.taskScheduler = taskScheduler;
        this.textUtil = textUtil;
        this.telegramBot = telegramBot;
        this.userService = userService;
    }

    public void startTask() {
        stopTask();
        if (isSchedulerEnabled) {
            future = taskScheduler.schedule(this::sendPhotoWithCaption, new CronTrigger(cronExpression));
            log.info("Scheduler started");
        }
    }

    public void stopTask() {
        if (future != null) {
            future.cancel(false);
            log.info("Stopping task");
        }
    }

    public void updateCronExpression(String newCronExpression) {
        this.cronExpression = newCronExpression;
        startTask();
        log.info("Updated Cron Expression: {}", cronExpression);
    }

    public void sendPhotoWithCaption() {
        int page = 0;
        int size = 50;
        Page<User> userPage;

        do {
            Pageable pageable = PageRequest.of(page, size);
            userPage = userService.getAll(pageable);
            List<User> currentBatchUsers = userPage.getContent();
            if (!currentBatchUsers.isEmpty()) {
                for (User user : currentBatchUsers) {
                    String textTopWeekProducts = textUtil.getTopWeekProducts(user.getUuid(), user.getTelegramUserEntity().getLanguage().getCode());

                    telegramBot.sendMessageWithMarkdown(user.getTelegramUserEntity().getChatId(), textTopWeekProducts);

                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    log.error("Error in delay: ", e);
                }

            }
            page++;

        } while (userPage.hasNext());

    }


    @PostConstruct
    public void init() {
        startTask();
    }
}
