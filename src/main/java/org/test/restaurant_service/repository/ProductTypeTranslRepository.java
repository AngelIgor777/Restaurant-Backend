package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.ProductTypeTransl;

import java.util.Optional;

public interface ProductTypeTranslRepository extends JpaRepository<ProductTypeTransl, Integer> {
    Optional<ProductTypeTransl> findByProductTypeIdAndLanguageCode(Integer productTypeId, String languageCode);
    Optional<ProductTypeTransl> findByProductType_NameAndLanguageCode(String productTypeName, String languageCode);
    Optional<ProductTypeTransl> findByNameAndLanguageCode(String productTypeName, String languageCode);
}
