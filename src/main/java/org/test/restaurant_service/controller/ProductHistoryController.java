package org.test.restaurant_service.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.ProductHistoryResponseDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.mapper.ProductHistoryMapper;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.service.ProductHistoryRevertService;
import org.test.restaurant_service.service.ProductHistoryService;
import org.test.restaurant_service.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productHistory")
public class ProductHistoryController {

    private final ProductHistoryService productHistoryService;
    private final ProductHistoryMapper productHistoryMapper;
    private final ProductHistoryRevertService productHistoryRevertService;

    public ProductHistoryController(ProductHistoryService productHistoryService, ProductHistoryMapper productHistoryMapper, @Qualifier("productServiceWithS3Impl") ProductService productService, ProductMapper productMapper, ProductHistoryRevertService productHistoryRevertService) {
        this.productHistoryService = productHistoryService;
        this.productHistoryMapper = productHistoryMapper;

        this.productHistoryRevertService = productHistoryRevertService;
    }

    @GetMapping("{productId}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public List<ProductHistoryResponseDTO> getProductHistory(@PathVariable Integer productId) {
        return productHistoryService.getProductHistoryByProductId(productId)
                .stream()
                .map(productHistoryMapper::toProductHistoryResponseDTO)
                .toList();
    }

    @PostMapping("/{productId}/{historyId}")
    public ProductResponseDTO revertToProductHistory(@PathVariable Integer productId, @PathVariable Integer historyId) {
        return productHistoryRevertService.revertProductToPickedProductHistory(productId, historyId);
    }


}
