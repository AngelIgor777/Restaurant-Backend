package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.ProductHistory;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Integer> {
}
