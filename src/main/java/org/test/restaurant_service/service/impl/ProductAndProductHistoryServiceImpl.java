package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.service.ProductAndProductHistoryService;
import org.test.restaurant_service.service.ProductHistoryService;
import org.test.restaurant_service.service.ProductService;

@Service
public class ProductAndProductHistoryServiceImpl implements ProductAndProductHistoryService {

    private final ProductService productService;
    private final ProductHistoryService productHistoryService;

    public ProductAndProductHistoryServiceImpl(@Qualifier("productServiceImpl") ProductService productService, ProductHistoryService productHistoryService) {
        this.productService = productService;
        this.productHistoryService = productHistoryService;
    }


    @Override
    public Product save(Product product, Integer typeId) {
        Product createdProduct = productService.create(product, typeId);
        productHistoryService.saveToProductHistory(createdProduct);
        return createdProduct;
    }

    @Override
    public Product update(Product product, Integer id) {
        Product update = productService.update(product, id);
        productHistoryService.saveToProductHistory(update);
        return update;
    }
}
