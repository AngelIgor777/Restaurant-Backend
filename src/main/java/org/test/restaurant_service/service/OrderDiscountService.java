package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.OrderDiscount;

import java.util.List;

public interface OrderDiscountService {

    // Find all OrderDiscounts
    List<OrderDiscount> findAll();

    // Find by ID
    OrderDiscount findById(Integer id);

    // Save or update an OrderDiscount
    OrderDiscount save(OrderDiscount orderDiscount);

    // Delete by ID
    void deleteById(Integer id);

    // Find by Order ID
    OrderDiscount findByOrderId(Integer orderId);

    // Find by Discount ID
    List<OrderDiscount> findByDiscountId(Integer discountId);

    // Check if a record exists by Order ID and Discount ID
    boolean existsByOrderIdAndDiscountId(Integer orderId, Integer discountId);
    boolean existsByOrderId(Integer orderId);
}
