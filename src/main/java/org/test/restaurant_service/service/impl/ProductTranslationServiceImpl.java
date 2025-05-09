package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.ProductTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTranslationResponseDTO;
import org.test.restaurant_service.entity.ProductTranslation;
import org.test.restaurant_service.mapper.ProductTranslationMapper;
import org.test.restaurant_service.repository.ProductTranslationRepository;
import org.test.restaurant_service.service.ProductTranslationService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTranslationServiceImpl implements ProductTranslationService {

    private final ProductTranslationRepository translationRepository;
    private final ProductTranslationMapper translationMapper;
    private final ProductTranslationRepository productTranslationRepository;

    @Override
    public ProductTranslationResponseDTO getTranslation(Integer productId, String languageCode) {
        ProductTranslationResponseDTO productTranslationResponseDTO = translationRepository.findProductTranslationByProduct_IdAndLanguageCode(productId, languageCode)
                .map(translationMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Product translation not found"));
        return productTranslationResponseDTO;
    }

    @Override
    public ProductTranslation getTranslationByProductName(String translationName) {
        return productTranslationRepository.findProductTranslationByName(translationName)
                .orElseThrow(() -> new EntityNotFoundException("Product translation not found"));
    }

    @Override
    public ProductTranslation getTranslationByProductId(Integer productId) {
        return productTranslationRepository.findProductTranslationByProduct_Id(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product translation not found"));
    }

    @Override
    public List<ProductTranslationResponseDTO> getAllTranslations(String languageCode) {
        return translationRepository.findAllByLanguageCode(languageCode).stream()
                .map(translationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductTranslationResponseDTO createOrUpdateTranslation(ProductTranslationRequestDTO requestDTO) {
        ProductTranslation translation = translationMapper.toEntity(requestDTO);
        ProductTranslation savedTranslation = translationRepository.save(translation);
        return translationMapper.toResponseDTO(savedTranslation);
    }

    @Override
    @Transactional
    public void deleteTranslation(Integer id) {
        translationRepository.deleteById(id);
    }
}
