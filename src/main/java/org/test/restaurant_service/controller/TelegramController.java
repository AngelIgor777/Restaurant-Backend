package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.dto.request.SendingMessageRequestDto;
import org.test.restaurant_service.service.SendingUsersService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tg")
public class TelegramController {
    private final SendingUsersService sendingUsersService;

    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public void sendMessage(@RequestBody SendingMessageRequestDto sendingMessageRequestDto) {
        sendingUsersService.sendMessageToAllTelegramUsers(sendingMessageRequestDto.getMessage());
    }
}
