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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTypeServiceImpl implements ProductTypeService {
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeMapper mapper;

    @Override
    public ProductTypeResponseDTO create(ProductTypeRequestDTO requestDTO) {
        ProductType productType = mapper.toEntity(requestDTO);
        productType = productTypeRepository.save(productType);

        return mapper.toResponseDTO(productType);
    }

    @Override
    public ProductTypeResponseDTO update(Integer id, ProductTypeRequestDTO requestDTO) {
        ProductType existingProductType = productTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductType not found with id " + id));
        mapper.updateEntityFromRequestDTO(requestDTO, existingProductType);
        existingProductType = productTypeRepository.save(existingProductType);
        return mapper.toResponseDTO(existingProductType);
    }

    @Override
    public void delete(Integer id) {
        if (!productTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("ProductType not found with id " + id);
        }
        productTypeRepository.deleteById(id);
    }

    @Override
    public ProductTypeResponseDTO getById(Integer id) {
        return mapper.toResponseDTO(getSimpleId(id));
    }

    @Override
    public ProductType getSimpleId(Integer id) {
        return productTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductType not found with id " + id));
    }

    @Override
    public Page<ProductTypeResponseDTO> getAll(Pageable pageable) {
        return productTypeRepository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    public List<ProductTypeResponseDTO> getAll() {
        return productTypeRepository.findAll().stream().map(mapper::toResponseDTO).toList();
    }

}
