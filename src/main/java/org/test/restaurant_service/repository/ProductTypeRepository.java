package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.dto.view.ProductTypeLocalizedView;
import org.test.restaurant_service.entity.ProductType;

import java.util.List;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Integer> {


    @Query(
            value = """
                    SELECT pt.id AS id,
                           COALESCE(pti.name, pt.name) AS name
                    FROM   restaurant_service.product_types pt
                    LEFT   JOIN restaurant_service.product_type_i18n pti
                           ON pt.id = pti.product_type_id AND pti.lang_id = :langId
                    """,
            nativeQuery = true)
    List<ProductTypeLocalizedView> findAllLocalized(@Param("langId") Integer langId);

    @Query(
            value = """
                    SELECT pt.id AS id,
                           COALESCE( pti.name, pt.name) AS name
                    FROM   restaurant_service.product_types pt
                    LEFT   JOIN restaurant_service.product_type_i18n pti
                           ON pt.id = pti.product_type_id AND pti.lang_id = :langId
                    WHERE pt.id = :id
                    """,
            nativeQuery = true)
    ProductTypeLocalizedView findOneLocalized(@Param("langId") Integer langId,
                                                    @Param("id") Integer id);
}
