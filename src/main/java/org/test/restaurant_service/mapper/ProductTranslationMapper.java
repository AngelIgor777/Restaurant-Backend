package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.test.restaurant_service.dto.request.ProductTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTranslationResponseDTO;
import org.test.restaurant_service.entity.ProductTranslation;

@Mapper(componentModel = "spring")
public interface ProductTranslationMapper {

    @Mapping(source = "productId", target = "product.id")
    ProductTranslation toEntity(ProductTranslationRequestDTO dto);

    @Mapping(source = "product.id", target = "productId")
    ProductTranslationResponseDTO toResponseDTO(ProductTranslation entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "product", ignore = true)
    void updateEntityFromDTO(ProductTranslationRequestDTO dto, @MappingTarget ProductTranslation entity);
}
