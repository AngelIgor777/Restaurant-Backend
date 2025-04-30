package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslResponseDTO;
import org.test.restaurant_service.entity.ProductTypeTransl;
import org.test.restaurant_service.mapper.ProductTypeTransMapper;
import org.test.restaurant_service.repository.ProductTranslationRepository;
import org.test.restaurant_service.repository.ProductTypeTranslRepository;
import org.test.restaurant_service.service.ProductTypeTranslationService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductTypeTranslationServiceImpl implements ProductTypeTranslationService {

    private final ProductTypeTranslRepository productTypeTranslRepository;
    private final ProductTypeTransMapper productTypeTransMapper;
    private final ProductTranslationRepository productTranslationRepository;

    @Override
    @Cacheable(value = "productTypeTranslationResponseDTO", key = "#productTypeId + '-' + #languageCode")
    public ProductTypeTranslResponseDTO getTranslation(Integer productTypeId, String languageCode) {
        ProductTypeTranslResponseDTO productTypeTranslResponseDTO = productTypeTranslRepository.findByProductTypeIdAndLanguageCode(productTypeId, languageCode)
                .map(productTypeTransMapper::toTranslationDTO)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        return productTypeTranslResponseDTO;
    }

    @Override
    public ProductTypeTransl getTranslation(String productTypeName, String languageCode) {
        ProductTypeTransl productTypeTransl = productTypeTranslRepository.findByProductType_NameAndLanguageCode(productTypeName, languageCode)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        return productTypeTransl;
    }

    @Override
    public ProductTypeTransl getByRoTranslation(String productTypeName, String languageCode) {
        ProductTypeTransl productTypeTransl = productTypeTranslRepository.findByNameAndLanguageCode(productTypeName, languageCode)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        return productTypeTransl;
    }

    @Override
    @CacheEvict(value = "productTypeTranslationResponseDTO", allEntries = true)
    public ProductTypeTranslResponseDTO createTranslation(ProductTypeTranslationRequestDTO requestDTO) {
        ProductTypeTransl entity = productTypeTransMapper.toEntity(requestDTO);
        ProductTypeTransl savedEntity = productTypeTranslRepository.save(entity);
        return productTypeTransMapper.toTranslationDTO(savedEntity);
    }

    @Override
    @CacheEvict(value = "productTypeTranslationResponseDTO", allEntries = true)
    public ProductTypeTranslResponseDTO updateTranslation(Integer id, ProductTypeTranslationRequestDTO requestDTO) {
        ProductTypeTransl entity = productTypeTranslRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductTypeTranslation not found"));

        productTypeTransMapper.updateEntityFromRequestDTO(requestDTO, entity);
        ProductTypeTransl updatedEntity = productTypeTranslRepository.save(entity);
        return productTypeTransMapper.toTranslationDTO(updatedEntity);
    }

    @Override
    @CacheEvict(value = "productTypeTranslationResponseDTO", allEntries = true)
    public void deleteTranslation(Integer id) {
        if (!productTypeTranslRepository.existsById(id)) {
            throw new EntityNotFoundException("ProductTypeTranslation not found");
        }
        productTypeTranslRepository.deleteById(id);
    }
}
