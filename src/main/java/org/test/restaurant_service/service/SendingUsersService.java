package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.Discount;
import org.test.restaurant_service.entity.ProductDiscount;

public interface SendingUsersService {
    void sendDiscountMessages(ProductDiscount savedDiscount);
    void sendDiscountMessages(Discount savedDiscount);

}
