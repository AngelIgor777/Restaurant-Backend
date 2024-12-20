package org.test.restaurant_service.mapper;

import org.mapstruct.*;

import org.test.restaurant_service.dto.request.ProductTypeRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
import org.test.restaurant_service.entity.ProductType;

@Mapper(componentModel = "spring")
public interface ProductTypeMapper {

    ProductType toEntity(ProductTypeRequestDTO requestDTO);

    ProductTypeResponseDTO toResponseDTO(ProductType productType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequestDTO(ProductTypeRequestDTO requestDTO, @MappingTarget ProductType productType);
}
