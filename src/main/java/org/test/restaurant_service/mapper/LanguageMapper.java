package org.test.restaurant_service.mapper;

import org.mapstruct.Mapper;
import org.test.restaurant_service.dto.request.LanguageRequestDTO;
import org.test.restaurant_service.dto.response.LanguageResponseDTO;
import org.test.restaurant_service.entity.Language;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageResponseDTO toDto(Language language);
    Language toEntity(LanguageRequestDTO dto);
}
