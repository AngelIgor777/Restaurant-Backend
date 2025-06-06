package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.entity.User;

public interface RoleService {

    void ensureUserHasRole(User user, RoleName roleName);

    void ensureUserHasRole(Long chatId, RoleName roleName);

    void removeUserRole(User user, RoleName roleName);

    void removeUserRole(Long chatId, RoleName roleName);
}
