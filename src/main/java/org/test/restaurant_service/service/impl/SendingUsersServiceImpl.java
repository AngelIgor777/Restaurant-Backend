package org.test.restaurant_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Discount;
import org.test.restaurant_service.entity.ProductDiscount;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.SendingUsersService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.telegram.handling.TelegramBot;
import org.test.restaurant_service.telegram.util.TextUtil;

import java.util.List;
import java.util.concurrent.*;

@Service
public class SendingUsersServiceImpl implements SendingUsersService {

    private static final Logger log = LoggerFactory.getLogger(SendingUsersServiceImpl.class);
    private static final int PAGE_SIZE = 50;
    private static final int DELAY_SECONDS = 10;

    private final UserService userService;
    private final TextUtil textUtil;
    private final TelegramBot telegramBot;

    private final TaskExecutor taskExecutor;

    public SendingUsersServiceImpl(UserService userService, TextUtil textUtil, TelegramBot telegramBot, @Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        this.userService = userService;
        this.textUtil = textUtil;
        this.telegramBot = telegramBot;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void sendDiscountMessages(ProductDiscount savedDiscount) {
        sendMessageToUsers(user ->
                textUtil.getTextForProductDiscount(savedDiscount, user.getUuid(), user.getTelegramUserEntity().getLanguage().getCode())
        );
    }

    @Override
    public void sendDiscountMessages(Discount savedDiscount) {
        sendMessageToUsers(user ->
                textUtil.getTextForGlobalDiscount(savedDiscount, user.getUuid(), user.getTelegramUserEntity().getLanguage().getCode())
        );
    }

    @Override
    @Async
    public void sendMessageToAllTelegramUsers(String message) {
        sendMessageToUsers(user -> message);
    }

    private void sendMessageToUsers(MessageFormatter messageFormatter) {
        int allUserSending = 0;
        int page = 0;
        Page<User> userPage;
        do {
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            userPage = userService.getAll(pageable);
            List<User> currentBatchUsers = userPage.getContent();

            if (!currentBatchUsers.isEmpty()) {
                for (User user : currentBatchUsers) {
                    taskExecutor.execute(() -> sendTelegramMessage(user, messageFormatter));
                    allUserSending++;
                }
                asyncDelay();
            }
            page++;
        } while (userPage.hasNext());
        log.debug("Send message to {} users", allUserSending);
    }

    private void sendTelegramMessage(User user, MessageFormatter messageFormatter) {
        try {
            String message = messageFormatter.formatMessage(user);
            telegramBot.sendMessageWithMarkdown(user.getTelegramUserEntity().getChatId(), message);
        } catch (Exception e) {
            log.error("Failed to send discount message to user: {}", user.getUuid(), e);
        }
    }

    @Async
    public void asyncDelay() {
        try {
            TimeUnit.SECONDS.sleep(DELAY_SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error in delay: ", e);
        }
    }


    @FunctionalInterface
    private interface MessageFormatter {
        String formatMessage(User user);
    }
}
