package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductHistory;

public interface ProductHistoryService {
    ProductHistory getProductHistoryById(Integer id);

    void deleteProductHistoryById(Integer id);

    void saveToProductHistory(Product product);

}
