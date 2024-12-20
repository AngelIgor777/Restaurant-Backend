package org.test.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.test.restaurant_service.dto.request.ProductTypeRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;


public interface ProductTypeService {
    ProductTypeResponseDTO create(ProductTypeRequestDTO requestDTO);

    ProductTypeResponseDTO update(Integer id, ProductTypeRequestDTO requestDTO);

    void delete(Integer id);

    ProductTypeResponseDTO getById(Integer id);

    Page<ProductTypeResponseDTO> getAll(Pageable pageable);
}