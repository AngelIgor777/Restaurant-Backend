package org.test.restaurant_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.test.restaurant_service.dto.request.LanguageRequestDTO;
import org.test.restaurant_service.dto.response.LanguageResponseDTO;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.mapper.helper.LanguageHelper;

@Mapper(componentModel = "spring",uses = LanguageHelper.class)
public interface LanguageMapper {

    @Mapping(target = "isAvailable", source = "id", qualifiedByName = "langIsAvailable")
    LanguageResponseDTO toDto(Language language);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramUserEntity", ignore = true)
    Language toEntity(LanguageRequestDTO dto);
}
