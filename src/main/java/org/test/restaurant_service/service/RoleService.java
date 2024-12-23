package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.entity.User;

public interface RoleService {

    void ensureUserHasRole(User user, RoleName roleName);
}
