package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.service.ProductService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTO create(@Valid @RequestBody ProductRequestDTO requestDTO) {
        return productService.create(requestDTO);
    }

    @PatchMapping("/{id}")
    public ProductResponseDTO update(@PathVariable Integer id, @Valid @RequestBody ProductRequestDTO requestDTO) {
        return productService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        productService.delete(id);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable Integer id) {
        return productService.getById(id);
    }

    @GetMapping
    public Page<ProductResponseDTO> getAll(@RequestParam(required = false) Integer typeId, Pageable pageable) {
        return productService.getAll(typeId, pageable);
    }
}
