package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.OrderDiscount;

import java.util.List;

public interface OrderDiscountRepository extends JpaRepository<OrderDiscount, Integer> {

    OrderDiscount findByOrderId(Integer orderId);

    List<OrderDiscount> findByDiscountId(Integer discountId);

    boolean existsByOrderIdAndDiscountId(Integer orderId, Integer discountId);
}
