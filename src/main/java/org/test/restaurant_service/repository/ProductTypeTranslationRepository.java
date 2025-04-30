package org.test.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.translations.ProductTypeTranslation;

public interface ProductTypeTranslationRepository extends JpaRepository<ProductTypeTranslation, Integer> {
    Page<ProductTypeTranslation> findAllByProductType_Id(Integer typeId, Pageable p);

    boolean existsByProductType_IdAndLanguage_Id(Integer typeId, Integer langId);
}