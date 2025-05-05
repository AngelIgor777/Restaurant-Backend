package org.test.restaurant_service.mapper;

import org.mapstruct.Mapper;
import org.test.restaurant_service.dto.response.UiTranslationDTO;
import org.test.restaurant_service.entity.UiTranslation;

@Mapper(componentModel = "spring")
public interface UiTranslationMapper {
    UiTranslationDTO toDto(UiTranslation entity);
}
