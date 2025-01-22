package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.ProductDiscount;

import java.util.Optional;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {
    Optional<ProductDiscount> getProductDiscountByCode(String name);
}
