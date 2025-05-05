package org.test.restaurant_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
import org.test.restaurant_service.dto.view.ProductTypeLocalizedView;
import org.test.restaurant_service.mapper.ProductTypeMapper;
import org.test.restaurant_service.repository.ProductTypeRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTypeTranslReadService {

    private final ProductTypeRepository repo;
    private final ProductTypeMapper mapper;
    private final LanguageService languageService;


    public List<ProductTypeResponseDTO> list(String lang) {
        return repo.findAllLocalized(languageService.getLanguageByCode(lang).getId()).stream()
                .map(mapper::fromLocalized).toList();
    }

    public ProductTypeResponseDTO one(Integer id, String lang) {
        ProductTypeLocalizedView view = repo.findOneLocalized(id, languageService.getLanguageByCode(lang).getId());
        if (view == null) throw new EntityNotFoundException("ProductType not found");
        return mapper.fromLocalized(view);
    }
}
