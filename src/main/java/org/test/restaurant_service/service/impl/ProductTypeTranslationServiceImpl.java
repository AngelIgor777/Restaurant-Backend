package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.entity.ProductTypeTranslation;
import org.test.restaurant_service.mapper.ProductTypeTranslationMapper;
import org.test.restaurant_service.repository.ProductTypeTranslationRepository;
import org.test.restaurant_service.service.ProductTypeTranslationService;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductTypeTranslationServiceImpl implements ProductTypeTranslationService {

    private final ProductTypeTranslationRepository productTypeTranslationRepository;
    private final ProductTypeTranslationMapper productTypeTranslationMapper;

    @Override
    public Optional<ProductTypeTranslationResponseDTO> getTranslation(Integer productTypeId, String languageCode) {
        return productTypeTranslationRepository.findByProductTypeIdAndLanguageCode(productTypeId, languageCode)
                .map(productTypeTranslationMapper::toTranslationDTO);
    }

    @Override
    public ProductTypeTranslationResponseDTO createTranslation(ProductTypeTranslationRequestDTO requestDTO) {
        ProductTypeTranslation entity = productTypeTranslationMapper.toEntity(requestDTO);
        ProductTypeTranslation savedEntity = productTypeTranslationRepository.save(entity);
        return productTypeTranslationMapper.toTranslationDTO(savedEntity);
    }

    @Override
    public ProductTypeTranslationResponseDTO updateTranslation(Integer id, ProductTypeTranslationRequestDTO requestDTO) {
        ProductTypeTranslation entity = productTypeTranslationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductTypeTranslation not found"));

        productTypeTranslationMapper.updateEntityFromRequestDTO(requestDTO, entity);
        ProductTypeTranslation updatedEntity = productTypeTranslationRepository.save(entity);
        return productTypeTranslationMapper.toTranslationDTO(updatedEntity);
    }

    @Override
    public void deleteTranslation(Integer id) {
        if (!productTypeTranslationRepository.existsById(id)) {
            throw new EntityNotFoundException("ProductTypeTranslation not found");
        }
        productTypeTranslationRepository.deleteById(id);
    }
}
