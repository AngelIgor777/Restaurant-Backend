package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.ProductTypeRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
import org.test.restaurant_service.entity.ProductType;
import org.test.restaurant_service.mapper.ProductTypeMapper;
import org.test.restaurant_service.repository.ProductTypeRepository;
import org.test.restaurant_service.service.ProductTypeService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductTypeServiceImpl implements ProductTypeService {
    private final ProductTypeRepository repository;
    private final ProductTypeMapper mapper;

    @Override
    public ProductTypeResponseDTO create(ProductTypeRequestDTO requestDTO) {
        ProductType productType = mapper.toEntity(requestDTO);
        productType = repository.save(productType);
        return mapper.toResponseDTO(productType);
    }

    @Override
    public ProductTypeResponseDTO update(Integer id, ProductTypeRequestDTO requestDTO) {
        ProductType existingProductType = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductType not found with id " + id));
        mapper.updateEntityFromRequestDTO(requestDTO, existingProductType);
        existingProductType = repository.save(existingProductType);
        return mapper.toResponseDTO(existingProductType);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("ProductType not found with id " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public ProductTypeResponseDTO getById(Integer id) {
        ProductType productType = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductType not found with id " + id));
        return mapper.toResponseDTO(productType);
    }

    @Override
    public Page<ProductTypeResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }
}
