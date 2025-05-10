package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.DiscountRequestDTO;
import org.test.restaurant_service.dto.response.DiscountResponseDTO;
import org.test.restaurant_service.entity.Discount;
import org.test.restaurant_service.mapper.DiscountMapper;
import org.test.restaurant_service.service.DiscountService;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;
    private final DiscountMapper discountMapper;

    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<DiscountResponseDTO> createDiscount(
            @Valid @RequestBody DiscountRequestDTO discountRequestDTO) {
        Discount discount = discountMapper.toEntity(discountRequestDTO);
        Discount createdDiscount = discountService.createDiscount(discount);
        return new ResponseEntity<>(discountMapper.toResponseDTO(createdDiscount), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountResponseDTO> getDiscountById(@PathVariable Integer id) {
        Discount discount = discountService.getDiscountById(id);
        return ResponseEntity.ok(discountMapper.toResponseDTO(discount));
    }

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<List<DiscountResponseDTO>> getAllDiscounts() {
        List<Discount> discounts = discountService.getAllDiscounts();
        List<DiscountResponseDTO> response = discounts.stream()
                .map(discountMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<DiscountResponseDTO> updateDiscount(
            @PathVariable Integer id,
            @Valid @RequestBody DiscountRequestDTO discountRequestDTO) {
        Discount existingDiscount = discountService.getDiscountById(id);
        discountMapper.updateEntityFromRequestDTO(discountRequestDTO, existingDiscount);
        Discount updatedDiscount = discountService.updateDiscount(id, existingDiscount);
        return ResponseEntity.ok(discountMapper.toResponseDTO(updatedDiscount));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Integer id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}
