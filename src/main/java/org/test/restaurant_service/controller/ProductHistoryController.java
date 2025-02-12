package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.response.ProductHistoryResponseDTO;
import org.test.restaurant_service.mapper.ProductHistoryMapper;
import org.test.restaurant_service.service.ProductHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productHistory")
@RequiredArgsConstructor
public class ProductHistoryController {

    private final ProductHistoryService productHistoryService;
    private final ProductHistoryMapper productHistoryMapper;


    @GetMapping("{productId}")
    public List<ProductHistoryResponseDTO> getProductHistory(@PathVariable Integer productId) {
        return productHistoryService.getProductHistoryByProductId(productId)
                .stream()
                .map(productHistoryMapper::toProductHistoryResponseDTO)
                .toList();
    }
}
