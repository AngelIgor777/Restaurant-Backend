package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslResponseDTO;
import org.test.restaurant_service.entity.ProductTypeTransl;

@Mapper(componentModel = "spring")
public interface ProductTypeTransMapper {

    @Mapping(source = "productType.id", target = "productTypeId")
    ProductTypeTranslResponseDTO toTranslationDTO(ProductTypeTransl productTypeTransl);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productType.id", source = "productTypeId")
    ProductTypeTransl toEntity(ProductTypeTranslationRequestDTO requestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequestDTO(ProductTypeTranslationRequestDTO requestDTO, @MappingTarget ProductTypeTransl entity);
}
