package org.test.restaurant_service.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.dto.response.UserInfoResponse;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.mapper.AddressMapper;
import org.test.restaurant_service.mapper.UserMapper;
import org.test.restaurant_service.service.UserService;

@Service
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final UserService userService;

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
}
