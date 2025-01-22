package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.Discount;

import java.util.List;
import java.util.Optional;

public interface DiscountService {

    /**
     * Creates a new discount.
     *
     * @param discount the discount entity to create
     * @return the created discount
     */
    Discount createDiscount(Discount discount);

    /**
     * Retrieves a discount by its ID.
     *
     * @param id the ID of the discount
     * @return an Optional containing the discount if found, or empty otherwise
     */
    Discount getDiscountById(Integer id);

    Discount getDiscountByCode(String code);

    /**
     * Retrieves all discounts.
     *
     * @return a list of all discounts
     */
    List<Discount> getAllDiscounts();

    /**
     * Updates an existing discount.
     *
     * @param id       the ID of the discount to update
     * @param discount the updated discount entity
     * @return the updated discount
     */
    Discount updateDiscount(Integer id, Discount discount);

    /**
     * Deletes a discount by its ID.
     *
     * @param id the ID of the discount to delete
     */
    void deleteDiscount(Integer id);
}
