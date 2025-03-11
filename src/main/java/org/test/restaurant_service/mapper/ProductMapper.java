package org.test.restaurant_service.mapper;


import org.mapstruct.*;

import org.mapstruct.factory.Mappers;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductHistory;

@Mapper(componentModel = "spring", uses = ProductTypeMapper.class)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "typeId", target = "type.id")
    @Mapping(target = "id", ignore = true) // Игнорирование id при обновлении
    @Mapping(target = "photos", ignore = true)
    Product

    toEntity(ProductRequestDTO requestDTO);

    @Mapping(source = "type.name", target = "typeName")
    @Mapping(target = "quantity", ignore = true)
    ProductResponseDTO toResponseDTO(Product product);

    @Mapping(source = "type.name", target = "typeName")
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "photos", ignore = true)
    ProductResponseDTO toResponseForTg(Product product);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "changedAt", ignore = true)
    ProductHistory toProductHistory(Product product);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photos", ignore = true)
    void updateEntityFromRequestDTO(ProductRequestDTO requestDTO, @MappingTarget Product product);
}