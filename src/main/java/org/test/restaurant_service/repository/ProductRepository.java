package org.test.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
}