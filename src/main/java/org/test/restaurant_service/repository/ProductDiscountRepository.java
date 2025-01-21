package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.ProductDiscount;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {
}
