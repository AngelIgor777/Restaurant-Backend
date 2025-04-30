package org.test.restaurant_service.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.translations.ProductI18nRequestDTO;
import org.test.restaurant_service.dto.response.ProductI18nResponseDTO;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.translations.ProductI18n;
import org.test.restaurant_service.mapper.ProductI18nMapper;
import org.test.restaurant_service.repository.LanguageRepository;
import org.test.restaurant_service.repository.ProductI18nRepository;
import org.test.restaurant_service.repository.ProductRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductI18nService {

    private final ProductI18nRepository repo;
    private final ProductRepository productRepo;
    private final LanguageRepository langRepo;
    private final ProductI18nMapper mapper;

    /**
     * Список переводов конкретного блюда с пагинацией
     */
    public Page<ProductI18nResponseDTO> list(Integer productId, Pageable pageable) {
        return repo.findAllByProduct_Id(productId, pageable)
                .map(mapper::toDto);
    }

    /**
     * Создание нового перевода
     */
    @Transactional
    public ProductI18nResponseDTO create(Integer productId, ProductI18nRequestDTO dto) {
        Product product = productRepo.getReferenceById(productId);
        Language language = langRepo.findById(dto.langId())
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));

        /* Проверяем уникальность (product + lang) */
        boolean exists = repo.existsByProduct_IdAndLanguage_Id(productId, dto.langId());
        if (exists) throw new IllegalStateException("Translation already exists");

        ProductI18n saved = repo.save(ProductI18n.builder()
                .product(product)
                .language(language)
                .name(dto.name())
                .description(dto.description())
                .build());

        return mapper.toDto(saved);
    }

    /**
     * Обновление существующего перевода
     */
    @Transactional
    public ProductI18nResponseDTO update(Integer id, ProductI18nRequestDTO dto) {
        ProductI18n entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        return mapper.toDto(entity);      // Hibernate сам синхронизирует изменения
    }

    /**
     * Удаление
     */
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}