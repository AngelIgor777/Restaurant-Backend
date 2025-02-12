package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductHistory;
import org.test.restaurant_service.entity.ProductType;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.repository.ProductHistoryRepository;
import org.test.restaurant_service.service.ProductHistoryService;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Service
@RequiredArgsConstructor

public class ProductHistoryServiceImpl implements ProductHistoryService {
    private final ProductHistoryRepository productHistoryRepository;
    private final ProductMapper productMapper;


    @Override
    public void saveToProductHistory(Product product) {
        ProductHistory productHistory = productMapper.toProductHistory(product);
        ProductType type = product.getType();
        productHistory.setProduct(product);
        productHistory.setType(type);
        productHistoryRepository.save(productHistory);
    }

    @Override
    public List<ProductHistory> getProductHistoryByProductId(Integer productId) {
        return productHistoryRepository.getProductHistoriesByProductId(productId);
    }

    @Override
    public ProductHistory getProductHistoryById(Integer id) {
        return productHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductHistory not found with ID: " + id));
    }

    @Override
    public void deleteProductHistoryById(Integer id) {
        if (!productHistoryRepository.existsById(id)) {
            throw new EntityNotFoundException("ProductHistory not found with ID: " + id);
        }
        productHistoryRepository.deleteById(id);
    }

}
