package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.TableOrderScore;


import java.util.List;
import java.util.UUID;

@Repository
public interface TableOrderScoreRepository extends JpaRepository<TableOrderScore, Integer> {

    List<TableOrderScore> findAllBySessionUUID(UUID sessionUUID);

    void deleteAllByTable_Id(Integer tableId);

    @Query(value = "SELECT CAST(session_uuid AS TEXT) " +
            "FROM restaurant_service.tables_order_score " +
            "GROUP BY session_uuid " +
            "ORDER BY MIN(created_at) ASC", nativeQuery = true)
    List<String> findUniqueSessionUUIDsOrderedByFirstCreatedAtDesc();

}