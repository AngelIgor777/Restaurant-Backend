package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.entity.ProductDiscount;
import org.test.restaurant_service.service.ProductDiscountService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product-discounts")
public class ProductDiscountController {

    private final ProductDiscountService productDiscountService;


    @PostMapping
    public ResponseEntity<ProductDiscount> createProductDiscount(@RequestBody ProductDiscount productDiscount) {
        return ResponseEntity.ok(productDiscountService.saveProductDiscount(productDiscount));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDiscount> getProductDiscountById(@PathVariable Integer id) {
        return ResponseEntity.ok(productDiscountService.getProductDiscountById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductDiscount>> getAllProductDiscounts() {
        return ResponseEntity.ok(productDiscountService.getAllProductDiscounts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDiscount> updateProductDiscount(@PathVariable Integer id, @RequestBody ProductDiscount productDiscount) {
        return ResponseEntity.ok(productDiscountService.updateProductDiscount(id, productDiscount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductDiscountById(@PathVariable Integer id) {
        productDiscountService.deleteProductDiscountById(id);
        return ResponseEntity.noContent().build();
    }
}
