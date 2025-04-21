package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.telegram.handling.TelegramBot;
import org.test.restaurant_service.telegram.util.TextUtil;

import javax.validation.constraints.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/connection")
@Validated
public class ConnectionController {

    private final TelegramBot telegramBot;
    private final UserService userService;
    private final TextUtil textUtil;

    @PostMapping
    public void sendConnection(
            @RequestParam @NotBlank @Size(max = 100) String name,
            @RequestParam @Email @NotBlank String email,
            @RequestParam @NotBlank @Size(max = 100) String event,
            @RequestParam
            @NotBlank
            String phoneNumber,
            @RequestParam @NotBlank @Size(max = 200) String message
    ) {
        sendToAdmins(name, email, event, phoneNumber, message);
    }

    @Async
    public void sendToAdmins(String name, String email, String event, String phoneNumber, String message) {
        List<User> allAdminsAndModerators = userService.getAllAdminsAndModerators();
        for (User user : allAdminsAndModerators) {
            String textForConnection = textUtil.getTextForConnection(name, email, event, phoneNumber, message, user.getTelegramUserEntity().getLanguage().getCode());
            telegramBot.sendMessageWithHTML(user.getTelegramUserEntity().getChatId(), textForConnection);
        }
    }
}
