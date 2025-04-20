package org.test.restaurant_service.service.impl.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.table.OpenTables;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TableCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper jacksonObjectMapper;

    public UUID addTableToOpen(Integer tableId) {
        OpenTables openTables = getOpenTables();
        if (openTables == null) {
            openTables = new OpenTables();
            openTables.setIds(new HashSet<>());
        }
        Set<Integer> ids = openTables.getIds();
        if (ids.contains(tableId)) {
            return getSessionUUID(tableId);
        } else {
            ids.add(tableId);
            saveOpenTables(openTables);
            return generateSessionUUID(tableId);
        }
    }

    public UUID generateSessionUUID(Integer tableId) {
        UUID sessionUUID = UUID.randomUUID();
        redisTemplate.opsForValue().set("session:" + tableId, sessionUUID);
        return sessionUUID;
    }

    public UUID getSessionUUID(Integer tableId) {
        Object object = redisTemplate.opsForValue().get("session:" + tableId);
        return jacksonObjectMapper.convertValue(object, UUID.class);
    }

    public UUID deleteSessionUUID(Integer tableId) {
        Object object = redisTemplate.opsForValue().getAndDelete(("session:" + tableId));
        return jacksonObjectMapper.convertValue(object, UUID.class);
    }


    public void saveOpenTables(OpenTables openTables) {
        redisTemplate.opsForValue().set("openTables", openTables);
    }

    public OpenTables getOpenTables() {
        Object object = redisTemplate.opsForValue().get("openTables");
        OpenTables openTables = jacksonObjectMapper.convertValue(object, OpenTables.class);
        if (openTables == null) {
            openTables = new OpenTables();
            openTables.setIds(new HashSet<>());
        }
        return openTables;
    }

    public void deleteTableFromClosed(Integer tableId) {
        OpenTables closedTables = getOpenTables();
        if (closedTables != null && closedTables.getIds().contains(tableId)) {
            closedTables.getIds().remove(tableId);
            saveOpenTables(closedTables);
        }
    }

    public void addOrderIdToTable(Integer orderId, Integer tableId) {
        Set<Integer> tableOrders = getTableOrders(tableId);
        if (tableOrders == null) {
            tableOrders = new HashSet<>();
        }
        tableOrders.add(orderId);
        redisTemplate.opsForValue().set("tableOrders:" + tableId, tableOrders);
    }

    public List<TableOrderInfo> getAllTableOrderInfos() {
        Set<String> keys = redisTemplate.keys("tableOrders:*");
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }

        List<TableOrderInfo> tableOrderInfos = new ArrayList<>();

        for (String key : keys) {
            String tableIdStr = key.replace("tableOrders:", "");
            Integer tableId = Integer.valueOf(tableIdStr);

            Set<Integer> orderIds = getTableOrders(tableId);

            if (orderIds != null && !orderIds.isEmpty()) {
                TableOrderInfo tableOrderInfo = new TableOrderInfo();
                tableOrderInfo.setTableId(tableId);
                tableOrderInfos.add(tableOrderInfo);
            }
        }
        return tableOrderInfos;
    }


    public Set<Integer> getTableOrders(Integer tableId) {
        Object object = redisTemplate.opsForValue().get("tableOrders:" + tableId);
        if (object == null) {
            return new HashSet<>();
        }
        return jacksonObjectMapper.convertValue(object, new TypeReference<Set<Integer>>() {
        });
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
