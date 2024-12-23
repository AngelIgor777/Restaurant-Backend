package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Role;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.RoleRepository;
import org.test.restaurant_service.service.RoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public void ensureUserHasRole(User user, RoleName roleName) {
        // Проверяем, есть ли роль у пользователя
        boolean hasRole = user.getRoles()
                .stream()
                .anyMatch(role -> role.getRoleName().equals(roleName));
        if (!hasRole) {
            Role role = roleRepository.findRoleByRoleName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.getRoles().add(role);
            roleRepository.save(role);
        }
    }
}
