package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.dto.response.UserInfoResponse;
import org.test.restaurant_service.service.UserInfoService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final UserInfoService userInfoService;


    @GetMapping("/{userUUID}")
    public UserInfoResponse getUserInfo(@PathVariable UUID userUUID) {
        return userInfoService.getUserInfo(userUUID);

    }


}
