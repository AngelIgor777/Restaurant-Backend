package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.ProductDiscountRequestDTO;
import org.test.restaurant_service.dto.response.ProductDiscountResponseDTO;
import org.test.restaurant_service.entity.ProductDiscount;
import org.test.restaurant_service.mapper.ProductDiscountMapper;
import org.test.restaurant_service.service.ProductDiscountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product-discounts")
public class ProductDiscountController {

    private final ProductDiscountService productDiscountService;
    private final ProductDiscountMapper productDiscountMapper;

    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDiscountResponseDTO createProductDiscount(@RequestBody ProductDiscountRequestDTO productDiscountRequestDTO) {
        ProductDiscount productDiscount = productDiscountMapper.toEntity(productDiscountRequestDTO);
        ProductDiscount createdProductDiscount = productDiscountService.saveProductDiscount(productDiscount);

        return productDiscountMapper.toResponseDTO(createdProductDiscount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDiscountResponseDTO> getProductDiscountById(@PathVariable Integer id) {
        ProductDiscount productDiscountById = productDiscountService.getProductDiscountById(id);
        ProductDiscountResponseDTO responseDTO = productDiscountMapper.toResponseDTO(productDiscountById);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<List<ProductDiscountResponseDTO>> getAllProductDiscounts() {
        List<ProductDiscountResponseDTO> allProductDiscounts = productDiscountService.getAllProductDiscounts()
                .stream()
                .map(productDiscountMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(allProductDiscounts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<ProductDiscountResponseDTO> updateProductDiscount(@PathVariable Integer id, @RequestBody ProductDiscount productDiscount) {

        return ResponseEntity.ok(productDiscountMapper.toResponseDTO(productDiscountService.updateProductDiscount(id, productDiscount)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ResponseEntity<Void> deleteProductDiscountById(@PathVariable Integer id) {
        productDiscountService.deleteProductDiscountById(id);
        return ResponseEntity.noContent().build();
    }
}
