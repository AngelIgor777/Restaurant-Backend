package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.test.restaurant_service.dto.request.OrderRequestDTO;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.entity.Order;

@Mapper(componentModel = "spring", uses = {TableMapper.class})
public interface OrderMapper {

    @Mapping(target = "products", ignore = true) // Игнорирование при создании
    @Mapping(target = "totalCookingTime", ignore = true)
    @Mapping(target = "tableResponseDTO", ignore = true)
    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(target = "table", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Order toEntity(OrderRequestDTO requestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "table", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromRequestDTO(OrderRequestDTO requestDTO, @MappingTarget Order order);
}
