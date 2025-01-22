package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Discount;
import org.test.restaurant_service.repository.DiscountRepository;
import org.test.restaurant_service.service.DiscountService;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    public Discount createDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    @Override
    public Discount getDiscountById(Integer id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found"));
    }

    @Override
    public Discount getDiscountByCode(String code) {
        return discountRepository.findDiscountByCode(code).orElseThrow(() -> new EntityNotFoundException("Discount not found"));
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public Discount updateDiscount(Integer id, Discount discount) {
        return discountRepository.findById(id)
                .map(existingDiscount -> {
                    existingDiscount.setCode(discount.getCode());
                    existingDiscount.setDescription(discount.getDescription());
                    existingDiscount.setDiscount(discount.getDiscount());
                    existingDiscount.setValidFrom(discount.getValidFrom());
                    existingDiscount.setValidTo(discount.getValidTo());
                    return discountRepository.save(existingDiscount);
                })
                .orElseThrow(() -> new EntityNotFoundException("Discount with ID " + id + " not found"));
    }

    @Override
    public void deleteDiscount(Integer id) {
        discountRepository.deleteById(id);
    }
}
