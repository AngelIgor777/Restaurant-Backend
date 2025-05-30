package org.test.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.test.restaurant_service.dto.request.ProductTypeRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
import org.test.restaurant_service.entity.ProductType;

import java.util.List;


public interface ProductTypeService {
    ProductTypeResponseDTO create(ProductTypeRequestDTO requestDTO);

    ProductTypeResponseDTO update(Integer id, ProductTypeRequestDTO requestDTO);

    void delete(Integer id);

    ProductTypeResponseDTO getById(Integer id);
    ProductType getSimpleId(Integer id);

    Page<ProductTypeResponseDTO> getAll(Pageable pageable);
    List<ProductTypeResponseDTO> getAll( );
}