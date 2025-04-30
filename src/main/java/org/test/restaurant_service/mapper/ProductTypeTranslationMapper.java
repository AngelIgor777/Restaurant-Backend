package org.test.restaurant_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.entity.translations.ProductTypeTranslation;

@Mapper(componentModel = "spring")
public interface ProductTypeTranslationMapper {
    @Mapping(source = "productType.id", target = "productTypeId")
    @Mapping(source = "language.id",    target = "langId")
    ProductTypeTranslationResponseDTO toDto(ProductTypeTranslation e);
}
