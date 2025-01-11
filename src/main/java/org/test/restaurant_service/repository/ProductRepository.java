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

    @EntityGraph(attributePaths = {"photos","type"})
    @Query("SELECT DISTINCT p FROM Product  p WHERE  p.id = :id")
    Optional<Product> findByIdWithPhotos(@Param("id") Integer id);

}