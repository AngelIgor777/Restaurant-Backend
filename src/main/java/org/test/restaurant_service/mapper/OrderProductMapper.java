package org.test.restaurant_service.mapper;

import org.mapstruct.*;

import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseDTO;
import org.test.restaurant_service.entity.OrderProduct;

@Mapper(componentModel = "spring", uses = {OrderMapper.class, ProductMapper.class})
public interface OrderProductMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    OrderProductResponseDTO toResponseDTO(OrderProduct orderProduct);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderProduct toEntity(OrderProductRequestDTO requestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntityFromRequestDTO(OrderProductRequestDTO requestDTO, @MappingTarget OrderProduct orderProduct);
}
