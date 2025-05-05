package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.dto.view.ProductLocalizedView;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.service.LanguageService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTranslReadService {

    private final ProductRepository repo;
    private final ProductMapper mapper;
    private final LanguageService languageService;

    public Page<ProductResponseDTO> list(Integer typeId,
                                         String lang,
                                         Pageable pageable) {
        Integer langId = languageService.getLanguageByCode(lang).getId();
        Page<ProductLocalizedView> allLocalized = repo.findAllLocalized(langId, typeId, pageable);
        return allLocalized
                .map(mapper::fromProjection);
    }

    public ProductResponseDTO one(Integer id, String lang) {
        Integer langId = languageService.getLanguageByCode(lang).getId();

        ProductLocalizedView view = repo.findOneLocalized(id, langId);
        if (view == null) throw new EntityNotFoundException("Product not found");
        return mapper.fromProjection(view);
    }

    public List<ProductResponseDTO> topWeek(String lang, Pageable pageable) {
        Integer langId = languageService.getLanguageByCode(lang).getId();

        return repo.getTop10ProductsWeek(pageable).stream()
                .map(p -> mapper.fromProjection(
                        repo.findOneLocalized(p.getId(), langId)))
                .toList();
    }

}
