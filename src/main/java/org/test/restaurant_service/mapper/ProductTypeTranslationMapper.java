package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.entity.ProductTypeTranslation;

@Mapper(componentModel = "spring")
public interface ProductTypeTranslationMapper {

    @Mapping(source = "productType.id", target = "productTypeId")
    ProductTypeTranslationResponseDTO toTranslationDTO(ProductTypeTranslation productTypeTranslation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productType.id", source = "productTypeId")
    ProductTypeTranslation toEntity(ProductTypeTranslationRequestDTO requestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequestDTO(ProductTypeTranslationRequestDTO requestDTO, @MappingTarget ProductTypeTranslation entity);
}
