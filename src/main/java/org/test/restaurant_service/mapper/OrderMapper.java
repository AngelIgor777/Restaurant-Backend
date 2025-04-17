package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.entity.Order;

@Mapper(componentModel = "spring", uses = {TableMapper.class})
public interface OrderMapper {

    @Mapping(target = "products", ignore = true) // Игнорирование при создании
    @Mapping(target = "totalCookingTime", ignore = true)
    OrderResponseDTO toResponseDTO(Order order);

}
