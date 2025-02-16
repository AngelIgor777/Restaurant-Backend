package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Discount;
import org.test.restaurant_service.entity.ProductDiscount;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.SendingUsersService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.telegram.handling.TelegramBot;
import org.test.restaurant_service.telegram.util.TextUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SendingUsersServiceImpl implements SendingUsersService {

    private static final Logger log = LoggerFactory.getLogger(SendingUsersServiceImpl.class);
    private final UserService userService;
    private final TextUtil textUtil;
    private final TelegramBot telegramBot;

    @Override
    public void sendDiscountMessages(ProductDiscount savedDiscount) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);  // Thread pool with 10 workers
        int page = 0;
        int size = 50;  // Send 50 messages at a time
        Page<User> userPage;

        do {
            Pageable pageable = PageRequest.of(page, size);
            userPage = userService.getAll(pageable);
            List<User> currentBatchUsers = userPage.getContent();

            if (!currentBatchUsers.isEmpty()) {
                for (User user : currentBatchUsers) {
                    String textForDiscount = textUtil.getTextForProductDiscount(savedDiscount, user.getUuid());

                    executorService.submit(() -> {
                        try {
                            telegramBot.sendMessageWithMarkdown(user.getTelegramUserEntity().getChatId(), textForDiscount);
                        } catch (Exception e) {
                            log.error("Failed to send discount message", e);
                        }
                    });
                }

                // Pause for 10 seconds before sending the next batch
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    log.error("Error in delay: ", e);
                }
            }
            page++;
        } while (userPage.hasNext());

        // Shutdown executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    @Override
    public void sendDiscountMessages(Discount savedDiscount) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int page = 0;
        int size = 50;
        Page<User> userPage;

        do {
            Pageable pageable = PageRequest.of(page, size);
            userPage = userService.getAll(pageable);
            List<User> currentBatchUsers = userPage.getContent();

            if (!currentBatchUsers.isEmpty()) {
                for (User user : currentBatchUsers) {
                    String textForGlobalDiscount = textUtil.getTextForGlobalDiscount(savedDiscount, user.getUuid());

                    executorService.submit(() -> {
                        try {
                            telegramBot.sendMessageWithMarkdown(user.getTelegramUserEntity().getChatId(), textForGlobalDiscount);
                        } catch (Exception e) {
                            log.error("Failed to send discount message", e);
                        }
                    });
                }

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    log.error("Error in delay: ", e);
                }
            }
            page++;
        } while (userPage.hasNext());

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
