package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.entity.Role;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.RoleRepository;
import org.test.restaurant_service.service.RoleService;
import org.test.restaurant_service.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final StaffSendingOrderService staffSendingOrderService;

    private final UserService userService;

    @Override
    public void ensureUserHasRole(User user, RoleName roleName) {
        Role role = roleRepository.findRoleByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        if (user.getRoles() != null) {
            boolean hasRole = user.getRoles()
                    .stream()
                    .anyMatch(userRole -> userRole.getRoleName().equals(roleName));
            if (!hasRole) {
                user.getRoles().add(role);
            }
        } else {
            List<Role> userRoles = List.of(role);
            user.setRoles(userRoles);
        }
    }

    @Override
    public void ensureUserHasRole(Long chatId, RoleName roleName) {
        User user = userService.findByChatId(chatId);
        ensureUserHasRole(user, roleName);
        userService.save(user);
        switch (roleName.name()) {
            case "ROLE_COOK": {
                staffSendingOrderService.createStaffSendingOrder(user, chatId);
            }
            case "ROLE_MODERATOR": {
                staffSendingOrderService.createStaffSendingOrder(user, chatId);
            }
        }
    }

    @Override
    public void removeUserRole(User user, RoleName roleName) {
        if (user.getRoles() != null) {
            user.getRoles().removeIf(r -> r.getRoleName().equals(roleName));
        }
    }

    @Override
    @Transactional
    public void removeUserRole(Long chatId, RoleName roleName) {
        User user = userService.findByChatId(chatId);
        removeUserRole(user, roleName);

        staffSendingOrderService.deleteStaffSendingOrder(user, chatId);
    }
}
