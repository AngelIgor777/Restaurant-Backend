package org.test.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.translations.ProductTypeI18n;

import java.util.Optional;

public interface ProductTypeI18nRepository extends JpaRepository<ProductTypeI18n, Integer> {
    Page<ProductTypeI18n> findAllByProductType_Id(Integer typeId, Pageable p);

    boolean existsByProductType_IdAndLanguage_Id(Integer typeId, Integer langId);

    Optional<ProductTypeI18n> getByProductType_IdAndLanguage_Id(Integer typeId, Integer langId);
}