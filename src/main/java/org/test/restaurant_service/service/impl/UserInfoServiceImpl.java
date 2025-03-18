package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.dto.response.UserInfoResponse;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.mapper.AddressMapper;
import org.test.restaurant_service.service.UserInfoService;
import org.test.restaurant_service.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserService userService;
    private final AddressMapper addressMapper;

    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(UUID userUUID) {
        User user = userService.findByUUID(userUUID);

        TelegramUserEntity telegramUserEntity = user.getTelegramUserEntity();
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setUuid(userUUID);

        Address address;
        if (user.getAddress() != null) {
            address = user.getAddress();
            AddressResponseDTO responseDto = addressMapper.toResponseDto(address);
            responseDto.setUserUUID(userUUID);
            userInfoResponse.setAddressResponseDTO(responseDto);
        }
        String photoUrl;
        if ((photoUrl = telegramUserEntity.getPhotoUrl()) != null) {
            userInfoResponse.setPhotoUrl(photoUrl);
        }

        userInfoResponse.setChatId(telegramUserEntity.getChatId());
        userInfoResponse.setUsername(telegramUserEntity.getUsername());
        userInfoResponse.setFirstname(telegramUserEntity.getFirstname());

        return userInfoResponse;
    }
}
