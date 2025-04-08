package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.OrderDiscount;

import java.util.List;
import java.util.Optional;

public interface OrderDiscountRepository extends JpaRepository<OrderDiscount, Integer> {

    Optional<OrderDiscount> findByOrderId(Integer orderId);

    List<OrderDiscount> findByDiscountId(Integer discountId);

    boolean existsByOrderId(Integer orderId);

    boolean existsByOrderIdAndDiscountId(Integer orderId, Integer discountId);
}
