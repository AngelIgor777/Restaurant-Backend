package org.test.restaurant_service.mapper;

import org.test.restaurant_service.dto.response.UserInfoResponse;
import org.test.restaurant_service.dto.response.UserStaffResponseDTO;
import org.test.restaurant_service.entity.User;

public interface UserMapper {


    UserInfoResponse toUserInfoResponse(User user);

    UserStaffResponseDTO toUserStaffResponseDTO(User user);

}
