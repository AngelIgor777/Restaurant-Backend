package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.dto.response.UserInfoResponse;
import org.test.restaurant_service.mapper.UserMapper;
import org.test.restaurant_service.service.UserInfoService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.service.impl.CodeService;
import org.test.restaurant_service.service.impl.cache.CodeCacheService;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserInfoService userInfoService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final CodeService codeService;
    private final CodeCacheService codeCacheService;


    @GetMapping("/{userUUID}")
    @PreAuthorize("@securityService.userIsOwnerOrModeratorOrAdmin(authentication, #userUUID)")
    public UserInfoResponse getUserInfo(@PathVariable UUID userUUID) {
        return userInfoService.getUserInfo(userUUID);
    }

    @GetMapping("/search")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public Page<UserInfoResponse> search(@RequestParam String query, Pageable pageable) {
        return userService.search(query, pageable)
                .map(userMapper::toUserInfoResponse);
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateUser(@RequestParam UUID userUUID,
                                          @RequestParam int activationCode) {
        JwtResponse response = codeService.activateUser(userUUID, activationCode);
        ResponseCookie cookie = ResponseCookie.from("ACTIVATION_TOKEN", response.getToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(60))
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString()).build();
    }


    @PostMapping("/isActivated")
    public boolean isActivatedUser(@RequestParam UUID userUUID) {
        return codeCacheService.isUserActive(userUUID);
    }


}
