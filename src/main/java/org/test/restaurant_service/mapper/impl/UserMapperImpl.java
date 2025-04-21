package org.test.restaurant_service.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.dto.response.UserInfoResponse;
import org.test.restaurant_service.dto.response.UserStaffResponseDTO;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.entity.Role;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.mapper.AddressMapper;
import org.test.restaurant_service.mapper.UserMapper;
import org.test.restaurant_service.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    @Override
    public UserInfoResponse toUserInfoResponse(User user) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        Address address = user.getAddress();
        AddressResponseDTO responseDto = AddressMapper.INSTANCE.toResponseDto(address);
        userInfoResponse.setAddressResponseDTO(responseDto);
        TelegramUserEntity telegramUserEntity = user.getTelegramUserEntity();
        userInfoResponse.setUuid(user.getUuid());
        userInfoResponse.setChatId(telegramUserEntity.getChatId());
        userInfoResponse.setPhotoUrl(telegramUserEntity.getPhotoUrl());
        userInfoResponse.setUsername(telegramUserEntity.getUsername());
        userInfoResponse.setFirstname(telegramUserEntity.getFirstname());

        return userInfoResponse;
    }

    @Override
    public UserStaffResponseDTO toUserStaffResponseDTO(User user) {
        UserStaffResponseDTO userStaffResponseDTO = new UserStaffResponseDTO();
        TelegramUserEntity telegramUserEntity = user.getTelegramUserEntity();
        userStaffResponseDTO.setUuid(user.getUuid());
        userStaffResponseDTO.setChatId(telegramUserEntity.getChatId());
        userStaffResponseDTO.setUsername(telegramUserEntity.getUsername());
        userStaffResponseDTO.setFirstname(telegramUserEntity.getFirstname());
        userStaffResponseDTO.setPhotoUrl(telegramUserEntity.getPhotoUrl());
        List<String> userRoles = user.getRoles().stream()
                .map(role -> role.getRoleName().toString()).toList();
        userStaffResponseDTO.setRoles(userRoles);
        return userStaffResponseDTO;
    }
}
