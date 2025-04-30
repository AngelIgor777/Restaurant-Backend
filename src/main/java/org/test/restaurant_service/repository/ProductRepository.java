package org.test.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.dto.view.ProductLocalizedView;
import org.test.restaurant_service.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAllByTypeId(Integer typeId, Pageable pageable);

    List<Product> findAllByType_Name(String name);

    Optional<Product> findByName(String name);

    @EntityGraph(attributePaths = {"photos", "type"})
    @Query("SELECT DISTINCT p FROM Product  p WHERE  p.id = :id")
    Optional<Product> findByIdWithPhotos(@Param("id") Integer id);

    @Query("SELECT p FROM Product p " +
            "JOIN OrderProduct op ON p.id = op.product.id " +
            "JOIN Order o ON o.id = op.order.id " +
            "WHERE o.createdAt >= CURRENT_DATE - 7 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(op.id) DESC")
    List<Product> getTop10ProductsWeek(Pageable pageable);

    boolean existsByName(String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(String searchTerm, Pageable pageable);

    /* ------------- НОВОЕ ------------- */
    @Query("""
            SELECT  p.id                 AS id,
                    COALESCE(pi.name, p.name)               AS name,
                    COALESCE(pi.description, p.description) AS description,
                    COALESCE(ptt.name, pt.name)             AS typeName,
                    p.price              AS price,
                    p.cookingTime        AS cookingTime,
                    (SELECT  ph.url FROM Photo ph
                            WHERE ph.product = p ORDER BY ph.id LIMIT 1) AS photoUrl
            FROM           Product p
            JOIN           p.type           pt
            LEFT JOIN      ProductI18n pi   ON pi.product  = p
                                           AND pi.language.code = :lang
            LEFT JOIN      ProductTypeTranslation ptt
                                           ON ptt.productType = pt
                                           AND ptt.language.code = :lang
            WHERE (:typeId IS NULL OR pt.id = :typeId)
            """)
    Page<ProductLocalizedView> findAllLocalized(@Param("lang") String lang,
                                                @Param("typeId") Integer typeId,
                                                Pageable pageable);

    @Query("""
            SELECT  p.id,
                    COALESCE(pi.name, p.name),
                    COALESCE(pi.description, p.description),
                    COALESCE(ptt.name, pt.name),
                    p.price,
                    p.cookingTime,
//                   ( SELECT ph2.url
//                            FROM   Photo ph2
//                            WHERE  ph2.id = (
//                                SELECT MIN(ph3.id) FROM Photo ph3 WHERE ph3.product = p
//                            )
//                          ) AS photoUrl
            FROM           Product p
            JOIN           p.type           pt
            LEFT JOIN      ProductI18n pi   ON pi.product = p
                                           AND pi.language.code = :lang
            LEFT JOIN      ProductTypeTranslation ptt
                                           ON ptt.productType = pt
                                           AND ptt.language.code = :lang
            WHERE          p.id = :id
            """)
    ProductLocalizedView findOneLocalized(@Param("id") Integer id,
                                          @Param("lang") String lang);

}