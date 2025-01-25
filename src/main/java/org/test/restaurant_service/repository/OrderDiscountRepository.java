package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.OrderDiscount;

import java.util.List;

public interface OrderDiscountRepository extends JpaRepository<OrderDiscount, Integer> {

    // Find by Order ID
    OrderDiscount findByOrderId(Integer orderId);

    // Find by Discount ID
    List<OrderDiscount> findByDiscountId(Integer discountId);

    // Check existence by Order ID and Discount ID
    boolean existsByOrderIdAndDiscountId(Integer orderId, Integer discountId);
}
