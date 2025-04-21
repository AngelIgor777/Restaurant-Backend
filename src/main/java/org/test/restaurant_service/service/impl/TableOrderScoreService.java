package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.Table;
import org.test.restaurant_service.entity.TableOrderScore;
import org.test.restaurant_service.repository.TableOrderScoreRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableOrderScoreService {

    private final TableOrderScoreRepository scoreRepository;

    public void save(Table table, Order order, UUID sessionUUID) {
        TableOrderScore score = TableOrderScore.builder()
                .table(table)
                .order(order)
                .sessionUUID(sessionUUID)
                .build();
        scoreRepository.save(score);
    }


    public void deleteAllByTable(Table table) {
        if (table != null && table.getId() != null) {
            scoreRepository.deleteAllByTable_Id(table.getId());
        }
    }

    //must return uuids only for one day because every day db is cleaning and then admin
    // can see only sessions of the current day
    public List<String> getUUIDs() {
        List<String> uniqueSessionUUIDsOrderedByFirstCreatedAtDesc = scoreRepository.findUniqueSessionUUIDsOrderedByFirstCreatedAtDesc();
        log.info(uniqueSessionUUIDsOrderedByFirstCreatedAtDesc.toString());
        return uniqueSessionUUIDsOrderedByFirstCreatedAtDesc;
    }

}
