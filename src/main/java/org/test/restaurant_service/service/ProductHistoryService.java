package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductHistory;

import java.util.List;

public interface ProductHistoryService {
    ProductHistory getProductHistoryById(Integer id);

    void deleteProductHistoryById(Integer id);

    void saveToProductHistory(Product product);

    List<ProductHistory> getProductHistoryByProductId(Integer productId);


}
