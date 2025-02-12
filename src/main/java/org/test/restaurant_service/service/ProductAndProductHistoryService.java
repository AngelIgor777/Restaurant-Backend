package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.Product;

public interface ProductAndProductHistoryService {
    Product save(Product product, Integer typeId);
    Product update(Product product, Integer id);

}
