package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.service.ProductAndProductHistoryService;
import org.test.restaurant_service.service.ProductHistoryService;
import org.test.restaurant_service.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductAndProductHistoryServiceImpl implements ProductAndProductHistoryService {

    private final ProductService productService;
    private final ProductHistoryService productHistoryService;


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
