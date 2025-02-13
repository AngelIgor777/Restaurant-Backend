package org.test.restaurant_service.mapper.helper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.service.ProductService;

@Component
public class ProductDiscountMapperHelper {

    private final ProductService productService;


    public ProductDiscountMapperHelper(@Qualifier("productServiceWithS3Impl") ProductService productService) {
        this.productService = productService;
    }

    @Named("mapProductById")
    public Product mapProductById(Integer productId) {
        return productService.getSimpleById(productId);
    }
}
