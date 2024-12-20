package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductType;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.repository.ProductTypeRepository;
import org.test.restaurant_service.service.ProductService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO create(ProductRequestDTO requestDTO) {
        ProductType type = productTypeRepository.findById(requestDTO.getTypeId())
                .orElseThrow(() -> new EntityNotFoundException("ProductType not found with id " + requestDTO.getTypeId()));
        Product product = productMapper.toEntity(requestDTO);
        product.setType(type);
        product = productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO update(Integer id, ProductRequestDTO requestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));
        if (requestDTO.getTypeId() != null) {
            ProductType type = productTypeRepository.findById(requestDTO.getTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("ProductType not found with id " + requestDTO.getTypeId()));
            product.setType(type);
        }
        productMapper.updateEntityFromRequestDTO(requestDTO, product);
        product = productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    @Override
    public void delete(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseDTO getById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));
        return productMapper.toResponseDTO(product);
    }

    @Override
    public Page<ProductResponseDTO> getAll(Integer typeId, Pageable pageable) {
        Page<Product> products;
        if (typeId != null) {
            products = productRepository.findAllByTypeId(typeId, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }
        return products.map(productMapper::toResponseDTO);
    }
}
