package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.service.RoleService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RoleService roleService;

    @PreAuthorize("@securityService.authenticateAdmin(#adminCode1,#adminCode2)")
    @PostMapping("/addAdminRole")
    public void addAdminRole(@RequestParam Long chatId,
                             @RequestParam String adminCode1,
                             @RequestParam String adminCode2) {
        roleService.ensureUserHasRole(chatId, RoleName.ROLE_ADMIN);
    }
}
