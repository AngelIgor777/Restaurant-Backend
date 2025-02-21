package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.ProductTranslation;

import java.util.List;
import java.util.Optional;

public interface ProductTranslationRepository extends JpaRepository<ProductTranslation, Integer> {
    Optional<ProductTranslation> findByProductIdAndLanguageCode(Integer productId, String languageCode);
    Optional<ProductTranslation> findProductTranslationByProduct_IdAndLanguageCode(Integer productId, String languageCode);

    List<ProductTranslation> findAllByLanguageCode(String languageCode);

    Optional<ProductTranslation> findProductTranslationByName(String productTranslationName);
}
