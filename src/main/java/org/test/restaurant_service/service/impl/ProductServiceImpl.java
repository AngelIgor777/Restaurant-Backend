package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.dto.response.ProductAndPhotosResponseDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductType;
import org.test.restaurant_service.mapper.PhotoMapper;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.repository.ProductTypeRepository;
import org.test.restaurant_service.service.ProductService;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductMapper productMapper;
    private final PhotoMapper photoMapper;


    @Override
    public Product parseRequest(String name, String description, Integer typeId, BigDecimal price, String cookingTime) {
        ProductType type = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("ProductType not found with id :" + typeId));
        Product product = new Product();
        product.setName(name);
        product.setType(type);
        product.setDescription(description);
        product.setPrice(price);
        product.setCookingTime(LocalTime.parse(cookingTime));
        return product;
    }

    @Override
    public Product create(Product product, Integer typeId) {
        ProductType type = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("ProductType not found with id :" + typeId));
        product.setType(type);
        product = productRepository.save(product);
        return product;
    }

    @Override
    public Product update(Integer id, Product product) {
        product.setId(id);
        product = productRepository.save(product);
        return product;
    }

    @Override
    public void delete(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id " + id);
        }
        productRepository.deleteById(id);
    }


    @Override
    public ProductAndPhotosResponseDTO getById(Integer id) {
        Product product = productRepository.findByIdWithPhotos(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));
        List<PhotoResponseDTO> photoResponseDTOS = product.getPhotos()
                .stream()
                .map(photoMapper::toResponseDTO).toList();

        ProductResponseDTO productResponseDTO = productMapper.toResponseDTO(product);
        ProductAndPhotosResponseDTO productAndPhotosResponseDTO = new ProductAndPhotosResponseDTO(productResponseDTO, photoResponseDTOS);
        return productAndPhotosResponseDTO;
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

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getByName(String productName) {
        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with name " + productName));

        return productMapper.toResponseDTO(product);

    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getByTypeName(String typeName) {
        List<Product> allByTypeName = productRepository.findAllByType_Name(typeName);
        return allByTypeName.stream().map(productMapper::toResponseDTO).toList();
    }
}
