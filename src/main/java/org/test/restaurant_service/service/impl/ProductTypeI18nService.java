package org.test.restaurant_service.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.translations.ProductTypeTranslationRequestDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslationResponseDTO;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.entity.translations.ProductTypeI18n;
import org.test.restaurant_service.mapper.ProductTypeTranslationMapper;
import org.test.restaurant_service.repository.ProductTypeI18nRepository;
import org.test.restaurant_service.service.LanguageService;
import org.test.restaurant_service.service.ProductTypeService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductTypeI18nService {

    private final ProductTypeI18nRepository repo;
    private final ProductTypeService typeService;
    private final LanguageService languageService;
    private final ProductTypeTranslationMapper mapper;

    public Page<ProductTypeTranslationResponseDTO> list(Integer typeId, Pageable pageable) {
        return repo.findAllByProductType_Id(typeId, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public ProductTypeTranslationResponseDTO create(Integer typeId, ProductTypeTranslationRequestDTO dto) {
        Language language = languageService.getById(dto.langId());

        boolean exists = existsTranslation(typeId, language.getId());
        if (exists) throw new IllegalStateException("Translation already exists");

        ProductTypeI18n saved = repo.save(ProductTypeI18n.builder()
                .productType(typeService.getSimpleId(typeId))
                .language(language)
                .name(dto.name())
                .build());

        return mapper.toDto(saved);
    }

    public boolean existsTranslation(Integer typeId, Integer langId) {
        return repo.existsByProductType_IdAndLanguage_Id(typeId, langId);
    }

    @Transactional
    public ProductTypeTranslationResponseDTO update(Integer id, ProductTypeTranslationRequestDTO dto) {
        ProductTypeI18n entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        entity.setName(dto.name());
        return mapper.toDto(entity);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
