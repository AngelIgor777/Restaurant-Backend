package org.test.restaurant_service.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.Product;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAllByTypeId(Integer typeId, Pageable pageable);

    List<Product> findAllByType_Name(String name);

    Optional<Product> findByName(String name);

}