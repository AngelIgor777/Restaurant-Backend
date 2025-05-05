package org.test.restaurant_service.service.impl;


import org.springframework.beans.factory.annotation.Qualifier;
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
import org.test.restaurant_service.service.ProductService;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductI18nService {

    private final ProductI18nRepository repo;
    private final ProductService productRepo;
    private final LanguageRepository langRepo;
    private final ProductI18nMapper mapper;

    public ProductI18nService(ProductI18nRepository repo, @Qualifier("productServiceWithS3Impl") ProductService productRepo, LanguageRepository langRepo, ProductI18nMapper mapper) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.langRepo = langRepo;
        this.mapper = mapper;
    }


    @Transactional
    public ProductI18nResponseDTO create(Integer productId, ProductI18nRequestDTO dto) {
        Product product = productRepo.getSimpleById(productId);
        Language language = langRepo.findById(dto.langId())
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));

        Optional<ProductI18n> productI18nOptional = repo.getByProduct_IdAndLanguage_Id(productId, dto.langId());
        if (productI18nOptional.isPresent()) {
            ProductI18n productI18n = productI18nOptional.get();
            return update(productI18n.getId(), dto);
        }

        repo.findById(dto.langId());
        ProductI18n saved = repo.save(ProductI18n.builder()
                .product(product)
                .language(language)
                .name(dto.name())
                .description(dto.description())
                .build());

        return mapper.toDto(saved);
    }


    public ProductI18nResponseDTO update(Integer id, ProductI18nRequestDTO dto) {
        ProductI18n entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found"));
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        return mapper.toDto(entity);
    }


    public void delete(Integer id) {
        repo.deleteById(id);
    }


    public boolean existTranslation(Integer productId, Integer languageId) {
        return repo.existsByProduct_IdAndLanguage_Id(productId, languageId);
    }
}