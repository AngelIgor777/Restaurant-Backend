package org.test.restaurant_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.test.restaurant_service.dto.response.ProductHistoryResponseDTO;
import org.test.restaurant_service.entity.ProductHistory;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface ProductHistoryMapper {


    @Mapping(source = "type.name", target = "typeName")
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "id",source = "product.id")
    @Mapping(target = "productHistoryId",source = "id")
    @Mapping(target = "photoUrl", source = "product.photos", qualifiedByName = "mapPhotoUrl")
    ProductHistoryResponseDTO toProductHistoryResponseDTO(ProductHistory productHistory);

}
