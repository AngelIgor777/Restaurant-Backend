package org.test.restaurant_service.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.translations.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.entity.ProductType;
import org.test.restaurant_service.entity.translations.ProductTypeTranslation;
import org.test.restaurant_service.mapper.ProductTypeTranslationMapper;
import org.test.restaurant_service.repository.LanguageRepository;
import org.test.restaurant_service.repository.ProductTypeRepository;
import org.test.restaurant_service.repository.ProductTypeTranslationRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductTypeTranslationService {

    private final ProductTypeTranslationRepository repo;
    private final ProductTypeRepository typeRepo;
    private final LanguageRepository langRepo;
    private final ProductTypeTranslationMapper mapper;

    public Page<ProductTypeTranslationResponseDTO> list(Integer typeId, Pageable pageable) {
        return repo.findAllByProductType_Id(typeId, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public ProductTypeTranslationResponseDTO create(Integer typeId, ProductTypeTranslationRequestDTO dto) {
        ProductType productType = typeRepo.getReferenceById(typeId);
        Language language = langRepo.findById(dto.langId())
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));

        boolean exists = repo.existsByProductType_IdAndLanguage_Id(typeId, dto.langId());
        if (exists) throw new IllegalStateException("Translation already exists");

        ProductTypeTranslation saved = repo.save(ProductTypeTranslation.builder()
                .productType(productType)
                .language(language)
                .name(dto.name())
                .build());

        return mapper.toDto(saved);
    }

    @Transactional
    public ProductTypeTranslationResponseDTO update(Integer id, ProductTypeTranslationRequestDTO dto) {
        ProductTypeTranslation entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        entity.setName(dto.name());
        return mapper.toDto(entity);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
