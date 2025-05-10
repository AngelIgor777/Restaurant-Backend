package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.UserInfoResponse;
import org.test.restaurant_service.dto.response.UserStaffResponseDTO;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.mapper.UserMapper;
import org.test.restaurant_service.service.RoleService;
import org.test.restaurant_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("@securityService.authenticateAdmin(#adminCode1,#adminCode2)")
    @PostMapping("/addAdminRole")
    public void addAdminRole(@RequestParam Long chatId,
                             @RequestParam String adminCode1,
                             @RequestParam String adminCode2) {
        roleService.ensureUserHasRole(chatId, RoleName.ROLE_ADMIN);
    }

    @GetMapping("/staff")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public List<UserStaffResponseDTO> getStaff() {
        return userService.getStaff()
                .stream()
                .map(userMapper::toUserStaffResponseDTO)
                .toList();
    }

    @PreAuthorize("@securityService.checkPermissions(authentication, #role)")
    @PostMapping("/assignRole")
    public void assignUserRole(@RequestParam Long chatId, @RequestParam RoleName role) {
        if (role != RoleName.ROLE_COOK && role != RoleName.ROLE_MODERATOR) {
            throw new IllegalArgumentException("Invalid role. Allowed roles: COOK, MODERATOR");
        }
        roleService.ensureUserHasRole(chatId, role);
    }

}
