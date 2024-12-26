package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.entity.Photo;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PhotoMapper {

    @Mapping(source = "product.id", target = "productId")
    PhotoResponseDTO toResponseDTO(Photo photo);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true) // Игнорирование id при маппинге
    Photo toEntity(PhotoRequestDTO requestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true) // Игнорирование id при маппинге
    void updateEntityFromRequestDTO(PhotoRequestDTO requestDTO, @MappingTarget Photo photo);
}
