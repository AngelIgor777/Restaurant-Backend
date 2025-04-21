package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.test.restaurant_service.dto.request.DiscountRequestDTO;
import org.test.restaurant_service.dto.response.DiscountResponseDTO;
import org.test.restaurant_service.entity.Discount;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    DiscountResponseDTO toResponseDTO(Discount discount);

    @Mapping(target = "id", ignore = true) // Ignore ID for creation
    Discount toEntity(DiscountRequestDTO requestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequestDTO(DiscountRequestDTO requestDTO, @MappingTarget Discount discount);
}
