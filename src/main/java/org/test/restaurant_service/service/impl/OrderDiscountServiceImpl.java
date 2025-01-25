package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.entity.OrderDiscount;
import org.test.restaurant_service.repository.OrderDiscountRepository;
import org.test.restaurant_service.service.OrderDiscountService;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderDiscountServiceImpl implements OrderDiscountService {

    private final OrderDiscountRepository orderDiscountRepository;

    @Override
    public List<OrderDiscount> findAll() {
        return orderDiscountRepository.findAll();
    }

    @Override
    public OrderDiscount findById(Integer id) {
        return orderDiscountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("OrderDiscount not found with ID: " + id));
    }

    @Override
    @Transactional
    public OrderDiscount save(OrderDiscount orderDiscount) {
        if (orderDiscountRepository.existsByOrderIdAndDiscountId(
                orderDiscount.getOrder().getId(), orderDiscount.getDiscount().getId())) {
            throw new IllegalArgumentException("Duplicate order and discount combination is not allowed.");
        }
        return orderDiscountRepository.save(orderDiscount);
    }

    @Override
    public void deleteById(Integer id) {
        if (!orderDiscountRepository.existsById(id)) {
            throw new NoSuchElementException("OrderDiscount not found with ID: " + id);
        }
        orderDiscountRepository.deleteById(id);
    }

    @Override
    public OrderDiscount findByOrderId(Integer orderId) {
        return orderDiscountRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDiscount> findByDiscountId(Integer discountId) {
        return orderDiscountRepository.findByDiscountId(discountId);
    }

    @Override
    public boolean existsByOrderIdAndDiscountId(Integer orderId, Integer discountId) {
        return orderDiscountRepository.existsByOrderIdAndDiscountId(orderId, discountId);
    }

    @Override
    public boolean existsByOrderId(Integer orderId) {
      return   orderDiscountRepository.existsById(orderId);
    }
}
