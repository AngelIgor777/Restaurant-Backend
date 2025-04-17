package org.test.restaurant_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.test.restaurant_service.dto.response.TelegramUserDTO;
import org.test.restaurant_service.entity.TelegramUserEntity;

@Mapper(componentModel = "spring")
public interface TelegramUserMapper {

    TelegramUserMapper INSTANCE = Mappers.getMapper(TelegramUserMapper.class);

    TelegramUserDTO toDto(TelegramUserEntity entity);

}