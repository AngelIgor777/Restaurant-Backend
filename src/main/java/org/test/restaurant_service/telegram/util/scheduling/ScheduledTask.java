package org.test.restaurant_service.telegram.util.scheduling;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.service.PhotoService;
import org.test.restaurant_service.service.TelegramUserService;
import org.test.restaurant_service.telegram.util.TelegramBot;
import org.test.restaurant_service.telegram.util.TextService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
    private final PhotoService photoService;
    private final TelegramUserService telegramUserService;
    private final TaskScheduler taskScheduler;
    private final TextService textService;
    private final TelegramBot telegramBot;
    private ScheduledFuture<?> future;

    @Value("${scheduler.enabled:true}")
    private boolean isSchedulerEnabled;

    @Value("${scheduler.cron}") // Default value
    private String cronExpression;


    public void startTask() {
        stopTask(); // Stop any previous task
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
        startTask(); // Restart the task with the new cron expression
        log.info("Updated Cron Expression: {}", cronExpression);
    }

    public void sendPhotoWithCaption() {
        SendPhoto photo = new SendPhoto();
        Resource image = photoService.getImage("pizza.png");
        File file = null;
        try {
            file = image.getFile();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        photo.setPhoto(new InputFile(file));
        photo.setParseMode("HTML");

        List<TelegramUserEntity> all = telegramUserService.getAll();
        for (TelegramUserEntity telegramUserEntity : all) {

            String adCaption = textService.getCaptionForUser(telegramUserEntity);

            photo.setCaption(adCaption);
            try {
                photo.setChatId(telegramUserEntity.getChatId().toString());
                telegramBot.execute(photo);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }
}
