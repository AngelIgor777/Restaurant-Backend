package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.ProductTypeTranslation;

import java.util.Optional;

public interface ProductTypeTranslationRepository extends JpaRepository<ProductTypeTranslation, Integer> {
    Optional<ProductTypeTranslation> findByProductTypeIdAndLanguageCode(Integer productTypeId, String languageCode);
}
