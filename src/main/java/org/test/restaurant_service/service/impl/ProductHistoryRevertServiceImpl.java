package org.test.restaurant_service.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductHistory;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.service.ProductHistoryRevertService;
import org.test.restaurant_service.service.ProductHistoryService;
import org.test.restaurant_service.service.ProductService;

import java.util.ArrayList;

@Service
public class ProductHistoryRevertServiceImpl implements ProductHistoryRevertService {
    private final ProductHistoryService productHistoryService;
    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductHistoryRevertServiceImpl(ProductHistoryService productHistoryService, @Qualifier("productServiceWithS3Impl") ProductService productService, ProductMapper productMapper) {
        this.productHistoryService = productHistoryService;
        this.productService = productService;
        this.productMapper = productMapper;
    }


    public ProductResponseDTO revertProductToPickedProductHistory(Integer productId, Integer historyId) {
        ProductHistory productHistory = productHistoryService.getProductHistoryById(historyId);
        if (!productHistory.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Product history does not match the provided product ID");
        }

        Product product = productService.getSimpleById(productId);
        // Update product fields based on history
        product.setName(productHistory.getName());
        product.setDescription(productHistory.getDescription());
        product.setType(productHistory.getType());
        product.setPrice(productHistory.getPrice());
        product.setCookingTime(productHistory.getCookingTime());

        ArrayList<Photo> photos = new ArrayList<>();
        photos.add(Photo.builder()
                .url(productHistory.getPhotoUrl())
                .build());
        Product updatedProduct = productService.update(product, productId, photos);

        ProductResponseDTO productResponseDTO = productMapper.toResponseDTO(updatedProduct);
        return productResponseDTO;
    }

}
