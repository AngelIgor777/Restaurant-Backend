package org.test.restaurant_service.service.impl;

import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.ProductDiscount;
import org.test.restaurant_service.repository.ProductDiscountRepository;
import org.test.restaurant_service.service.ProductDiscountService;

import java.util.List;

@Service
public class ProductDiscountServiceImpl implements ProductDiscountService {

    private final ProductDiscountRepository productDiscountRepository;

    public ProductDiscountServiceImpl(ProductDiscountRepository productDiscountRepository) {
        this.productDiscountRepository = productDiscountRepository;
    }

    @Override
    public ProductDiscount saveProductDiscount(ProductDiscount productDiscount) {
        return productDiscountRepository.save(productDiscount);
    }

    @Override
    public ProductDiscount getProductDiscountById(Integer id) {
        return productDiscountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductDiscount not found with ID: " + id));
    }

    @Override
    public ProductDiscount getProductDiscountByCode(String name) {
        return productDiscountRepository.getProductDiscountByCode(name)
                .orElseThrow(() -> new RuntimeException("ProductDiscount not found with name: " + name));
    }

    @Override
    public List<ProductDiscount> getAllProductDiscounts() {
        return productDiscountRepository.findAll();
    }

    @Override
    public ProductDiscount updateProductDiscount(Integer id, ProductDiscount updatedDiscount) {
        ProductDiscount existingDiscount = getProductDiscountById(id);

        if (updatedDiscount.getCode() != null) {
            existingDiscount.setCode(updatedDiscount.getCode());
        }
        if (updatedDiscount.getDescription() != null) {
            existingDiscount.setDescription(updatedDiscount.getDescription());
        }
        if (updatedDiscount.getDiscount() != null) {
            existingDiscount.setDiscount(updatedDiscount.getDiscount());
        }
        if (updatedDiscount.getValidFrom() != null) {
            existingDiscount.setValidFrom(updatedDiscount.getValidFrom());
        }
        if (updatedDiscount.getValidTo() != null) {
            existingDiscount.setValidTo(updatedDiscount.getValidTo());
        }
        if (updatedDiscount.getProduct() != null) {
            existingDiscount.setProduct(updatedDiscount.getProduct());
        }

        return productDiscountRepository.save(existingDiscount);
    }

    @Override
    public void deleteProductDiscountById(Integer id) {
        if (!productDiscountRepository.existsById(id)) {
            throw new RuntimeException("ProductDiscount not found with ID: " + id);
        }
        productDiscountRepository.deleteById(id);
    }
}
