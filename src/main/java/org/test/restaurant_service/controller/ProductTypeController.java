package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.test.restaurant_service.dto.request.ProductTypeRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
import org.test.restaurant_service.service.ProductTypeService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/product-types")
@RequiredArgsConstructor
public class ProductTypeController {

    private final ProductTypeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductTypeResponseDTO create(@Valid @RequestBody ProductTypeRequestDTO requestDTO) {
        return service.create(requestDTO);
    }

    @PatchMapping("/{id}")
    public ProductTypeResponseDTO update(@PathVariable Integer id, @Valid @RequestBody ProductTypeRequestDTO requestDTO) {
        return service.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public ProductTypeResponseDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<ProductTypeResponseDTO> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }
}
