package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.ProductHistory;

import java.util.List;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Integer> {
    List<ProductHistory> getProductHistoriesByProductId(Integer productId);
}
