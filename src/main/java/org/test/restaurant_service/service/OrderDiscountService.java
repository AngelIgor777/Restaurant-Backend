package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.OrderDiscount;

import java.util.List;

public interface OrderDiscountService {

    List<OrderDiscount> findAll();

    OrderDiscount findById(Integer id);

    OrderDiscount save(OrderDiscount orderDiscount);

    void deleteById(Integer id);

    OrderDiscount findByOrderId(Integer orderId);

    List<OrderDiscount> findByDiscountId(Integer discountId);

    boolean existsByOrderIdAndDiscountId(Integer orderId, Integer discountId);

    boolean existsByOrderId(Integer orderId);
}
