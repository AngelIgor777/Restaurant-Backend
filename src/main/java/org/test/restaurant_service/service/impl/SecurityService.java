package org.test.restaurant_service.service.impl;

import org.springframework.stereotype.Service;
import org.test.restaurant_service.util.KeyUtil;

@Service
public class SecurityService {


    public boolean authenticateAdmin(String adminCode1, String adminCode2) {
        return (KeyUtil.getAdminCode1().equals(adminCode1) && KeyUtil.getAdminCode2().equals(adminCode2));
    }
}
