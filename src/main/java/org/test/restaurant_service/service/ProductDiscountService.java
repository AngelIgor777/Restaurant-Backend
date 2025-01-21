package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.ProductDiscount;

import java.util.List;

public interface ProductDiscountService {

    ProductDiscount saveProductDiscount(ProductDiscount productDiscount);

    ProductDiscount getProductDiscountById(Integer id);

    List<ProductDiscount> getAllProductDiscounts();

    ProductDiscount updateProductDiscount(Integer id, ProductDiscount productDiscount);

    void deleteProductDiscountById(Integer id);
}
