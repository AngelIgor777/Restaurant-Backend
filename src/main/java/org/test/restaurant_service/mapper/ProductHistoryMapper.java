package org.test.restaurant_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.test.restaurant_service.dto.response.ProductHistoryResponseDTO;
import org.test.restaurant_service.entity.ProductHistory;

@Mapper(componentModel = "spring")
public interface ProductHistoryMapper {


    @Mapping(source = "type.name", target = "typeName")
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "id",source = "product.id")
    @Mapping(target = "productHistoryId",source = "id")
    ProductHistoryResponseDTO toProductHistoryResponseDTO(ProductHistory productHistory);
}
