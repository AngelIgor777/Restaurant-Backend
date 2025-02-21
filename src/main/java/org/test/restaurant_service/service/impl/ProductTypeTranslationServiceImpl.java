package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.entity.ProductTypeTranslation;
import org.test.restaurant_service.mapper.ProductTypeTranslationMapper;
import org.test.restaurant_service.repository.ProductTranslationRepository;
import org.test.restaurant_service.repository.ProductTypeTranslationRepository;
import org.test.restaurant_service.service.ProductTypeTranslationService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductTypeTranslationServiceImpl implements ProductTypeTranslationService {

    private final ProductTypeTranslationRepository productTypeTranslationRepository;
    private final ProductTypeTranslationMapper productTypeTranslationMapper;
    private final ProductTranslationRepository productTranslationRepository;

    @Override
    @Cacheable(value = "productTypeTranslationResponseDTO", key = "#productTypeId + '-' + #languageCode")
    public ProductTypeTranslationResponseDTO getTranslation(Integer productTypeId, String languageCode) {
        ProductTypeTranslationResponseDTO productTypeTranslationResponseDTO = productTypeTranslationRepository.findByProductTypeIdAndLanguageCode(productTypeId, languageCode)
                .map(productTypeTranslationMapper::toTranslationDTO)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        return productTypeTranslationResponseDTO;
    }

    @Override
    public ProductTypeTranslation getTranslation(String productTypeName, String languageCode) {
        ProductTypeTranslation productTypeTranslation = productTypeTranslationRepository.findByProductType_NameAndLanguageCode(productTypeName, languageCode)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        return productTypeTranslation;
    }

    @Override
    public ProductTypeTranslation getByRoTranslation(String productTypeName, String languageCode) {
        ProductTypeTranslation productTypeTranslation = productTypeTranslationRepository.findByNameAndLanguageCode(productTypeName, languageCode)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        return productTypeTranslation;
    }

    @Override
    @CacheEvict(value = "productTypeTranslationResponseDTO", allEntries = true)
    public ProductTypeTranslationResponseDTO createTranslation(ProductTypeTranslationRequestDTO requestDTO) {
        ProductTypeTranslation entity = productTypeTranslationMapper.toEntity(requestDTO);
        ProductTypeTranslation savedEntity = productTypeTranslationRepository.save(entity);
        return productTypeTranslationMapper.toTranslationDTO(savedEntity);
    }

    @Override
    @CacheEvict(value = "productTypeTranslationResponseDTO", allEntries = true)
    public ProductTypeTranslationResponseDTO updateTranslation(Integer id, ProductTypeTranslationRequestDTO requestDTO) {
        ProductTypeTranslation entity = productTypeTranslationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductTypeTranslation not found"));

        productTypeTranslationMapper.updateEntityFromRequestDTO(requestDTO, entity);
        ProductTypeTranslation updatedEntity = productTypeTranslationRepository.save(entity);
        return productTypeTranslationMapper.toTranslationDTO(updatedEntity);
    }

    @Override
    @CacheEvict(value = "productTypeTranslationResponseDTO", allEntries = true)
    public void deleteTranslation(Integer id) {
        if (!productTypeTranslationRepository.existsById(id)) {
            throw new EntityNotFoundException("ProductTypeTranslation not found");
        }
        productTypeTranslationRepository.deleteById(id);
    }
}
