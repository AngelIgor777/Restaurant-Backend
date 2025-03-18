package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.UserInfoResponse;
import org.test.restaurant_service.mapper.UserMapper;
import org.test.restaurant_service.mapper.impl.UserMapperImpl;
import org.test.restaurant_service.service.UserInfoService;
import org.test.restaurant_service.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserInfoService userInfoService;

    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping("/{userUUID}")
    public UserInfoResponse getUserInfo(@PathVariable UUID userUUID) {
        return userInfoService.getUserInfo(userUUID);
    }

    @GetMapping("/search")
    public Page<UserInfoResponse> search(@RequestParam String query, Pageable pageable) {
        return userService.search(query, pageable)
                .map(userMapper::toUserInfoResponse);
    }
}
