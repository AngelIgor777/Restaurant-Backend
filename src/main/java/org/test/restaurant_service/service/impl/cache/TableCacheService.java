package org.test.restaurant_service.service.impl.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.table.OpenTables;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TableCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void addTableToOpen(Integer tableId) {
        OpenTables openTables = getOpenTables();
        if (openTables == null) {
            openTables = new OpenTables();
            openTables.setIds(new HashSet<>());
        }
        openTables.getIds().add(tableId);
        saveOpenTables(openTables);
    }

    public void saveOpenTables(OpenTables openTables) {
        redisTemplate.opsForValue().set("closedTables", openTables);
    }

    public OpenTables getOpenTables() {
        return (OpenTables) redisTemplate.opsForValue().get("openTables");
    }

    public void deleteTableFromClosed(Integer tableId) {
        OpenTables closedTables = getOpenTables();
        if (closedTables != null && closedTables.getIds().contains(tableId)) {
            closedTables.getIds().remove(tableId);
            saveOpenTables(closedTables);
        }
    }

    public void addOrderIdToTable(Integer tableId, Integer orderId) {
        Set<Integer> tableOrders = getTableOrders(tableId);
        if (tableOrders == null) {
            tableOrders = new HashSet<>();
        }
        tableOrders.add(orderId);
        redisTemplate.opsForValue().set("tableOrders:" + tableId, tableOrders);
    }

    public Set<Integer> getTableOrders(Integer tableId) {
        return (Set<Integer>) redisTemplate.opsForValue().get("tableOrders:" + tableId);
    }

    public void deleteOrderIdFromTable(Integer tableId, Integer orderId) {
        Set<Integer> tableOrders = getTableOrders(tableId);
        if (tableOrders != null) {
            tableOrders.remove(orderId);
        }
        redisTemplate.opsForValue().set("tableOrders:" + tableId, tableOrders);
    }

    public void deleteTableOrders(Integer tableId) {
        redisTemplate.delete("tableOrders:" + tableId);
    }

}
