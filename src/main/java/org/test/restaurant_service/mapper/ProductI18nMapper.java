package org.test.restaurant_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.test.restaurant_service.dto.response.ProductI18nResponseDTO;
import org.test.restaurant_service.entity.translations.ProductI18n;

@Mapper(componentModel = "spring")
public interface ProductI18nMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "language.id", target = "langId")
    ProductI18nResponseDTO toDto(ProductI18n e);
}