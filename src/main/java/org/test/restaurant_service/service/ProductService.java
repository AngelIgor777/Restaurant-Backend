package org.test.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductAndPhotosResponseDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;


public interface ProductService {
    ProductResponseDTO create(ProductRequestDTO requestDTO);

    ProductResponseDTO update(Integer id, ProductRequestDTO requestDTO);

    void delete(Integer id);

    ProductAndPhotosResponseDTO getById(Integer id);

    Page<ProductResponseDTO> getAll(Integer typeId, Pageable pageable);

    ProductResponseDTO getByName(String product);
}
