package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.response.UserInfoResponse;

import java.util.UUID;

public interface UserInfoService {

    UserInfoResponse getUserInfo(UUID userUUID);
}
