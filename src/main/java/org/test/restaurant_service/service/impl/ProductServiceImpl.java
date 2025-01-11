package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
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


    //TODO брать данные одним запросом
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
    public ProductResponseDTO getByName(String productName) {
        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with name " + productName));

        return productMapper.toResponseDTO(product);

    }

    public List<ProductResponseDTO> getByTypeName(String typeName) {
        List<Product> allByTypeName = productRepository.findAllByType_Name(typeName);
        return allByTypeName.stream().map(productMapper::toResponseDTO).toList();
    }
}
