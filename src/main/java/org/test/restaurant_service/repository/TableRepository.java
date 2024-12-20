package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.Table;

import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<Table, Integer> {
    Optional<Table> findTablesByNumber(Integer tableNumber);
}
