package org.test.restaurant_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.test.restaurant_service.dto.request.ProductDiscountRequestDTO;
import org.test.restaurant_service.dto.response.ProductDiscountResponseDTO;
import org.test.restaurant_service.entity.ProductDiscount;
import org.test.restaurant_service.mapper.helper.ProductDiscountMapperHelper;

@Mapper(componentModel = "spring", uses = ProductDiscountMapperHelper.class)
public interface ProductDiscountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "productId", qualifiedByName = "mapProductById")
    ProductDiscount toEntity(ProductDiscountRequestDTO productDiscountRequestDTO);


    @Mapping(target = "productId", source = "product.id")
    ProductDiscountResponseDTO toResponseDTO(ProductDiscount productDiscount);
}
