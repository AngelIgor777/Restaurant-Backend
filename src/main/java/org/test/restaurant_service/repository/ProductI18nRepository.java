package org.test.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.translations.ProductI18n;

public interface ProductI18nRepository extends JpaRepository<ProductI18n, Integer> {
    Page<ProductI18n> findAllByProduct_Id(Integer productId, Pageable p);

    boolean existsByProduct_IdAndLanguage_Id(Integer productId, Integer langId);

}