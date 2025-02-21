package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.Discount;

import java.util.List;

public interface DiscountService {


    Discount createDiscount(Discount discount);


    Discount getDiscountById(Integer id);

    Discount getDiscountByCode(String code);


    List<Discount> getAllDiscounts();


    Discount updateDiscount(Integer id, Discount discount);

    void deleteDiscount(Integer id);
}
