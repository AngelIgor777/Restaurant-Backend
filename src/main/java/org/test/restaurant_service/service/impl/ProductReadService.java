package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.dto.view.ProductLocalizedView;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.repository.ProductRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductReadService {

    private final ProductRepository repo;
    private final ProductMapper mapper;

    public Page<ProductResponseDTO> list(Integer typeId,
                                         String lang,
                                         Pageable pageable) {
        return repo.findAllLocalized(lang, typeId, pageable)
                .map(mapper::fromProjection);
    }

    public ProductResponseDTO one(Integer id, String lang) {
        ProductLocalizedView view = repo.findOneLocalized(id, lang);
        if (view == null) throw new EntityNotFoundException("Product not found");
        return mapper.fromProjection(view);
    }

    public List<ProductResponseDTO> topWeek(String lang, Pageable pageable) {
        return repo.getTop10ProductsWeek(pageable).stream()   // можно сделать аналогичный
                .map(p -> mapper.fromProjection(
                        repo.findOneLocalized(p.getId(), lang)))
                .toList();
    }
}
