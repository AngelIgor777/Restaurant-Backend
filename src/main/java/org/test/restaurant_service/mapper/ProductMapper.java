package org.test.restaurant_service.mapper;


import org.mapstruct.*;

import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Product;

@Mapper(componentModel = "spring", uses = ProductTypeMapper.class)
public interface ProductMapper {

    @Mapping(source = "typeId", target = "type.id")
    @Mapping(target = "id", ignore = true) // Игнорирование id при обновлении
    @Mapping(target = "cookingTime", ignore = true)
    Product toEntity(ProductRequestDTO requestDTO);

    @Mapping(source = "type.name", target = "typeName")
    ProductResponseDTO toResponseDTO(Product product);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "id", ignore = true) // Игнорирование id при обновлении
    @Mapping(target = "cookingTime", ignore = true)
    void updateEntityFromRequestDTO(ProductRequestDTO requestDTO, @MappingTarget Product product);
}