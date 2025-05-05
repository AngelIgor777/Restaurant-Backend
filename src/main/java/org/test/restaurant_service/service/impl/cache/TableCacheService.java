package org.test.restaurant_service.service.impl.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.table.OpenTables;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;
import org.test.restaurant_service.entity.Order;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TableCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper jacksonObjectMapper;

    public UUID addTableToOpen(Integer tableId) {
        OpenTables openTables = getOpenTables();
        Set<Integer> ids = openTables.getIds();
        if (ids.contains(tableId)) {
            return getSessionUUID(tableId);
        } else {
            ids.add(tableId);
            saveOpenTables(openTables);
            TableOrderInfo tableOrders = getTableOrders(tableId);
            saveTableOrders(tableId, tableOrders);
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

    public TableOrderInfo addOrderIdToTable(Integer orderId, Order.OrderStatus orderStatus, Integer tableId) {
        TableOrderInfo tableOrders = getTableOrders(tableId);

        tableOrders.setTableId(tableId);

        if (orderStatus.equals(Order.OrderStatus.COMPLETED)) {
            tableOrders.getCompletedOrders().add(orderId);
        } else if (orderStatus.equals(Order.OrderStatus.PENDING)) {
            tableOrders.getPendingOrders().add(orderId);
        } else if (orderStatus.equals(Order.OrderStatus.CONFIRMED)) {
            tableOrders.getConfirmedOrders().add(orderId);
        }

        saveTableOrders(tableId, tableOrders);
        return tableOrders;
    }

    private void saveTableOrders(Integer tableId, TableOrderInfo tableOrders) {
        redisTemplate.opsForValue().set("tableOrders:" + tableId, tableOrders);
    }

    public TableOrderInfo changeOrderStateForTable(Integer orderId, Integer tableId, Order.OrderStatus currentStatus, Order.OrderStatus changeStatus) {
        TableOrderInfo tableOrders = getTableOrders(tableId);
        if (currentStatus.equals(Order.OrderStatus.COMPLETED)) {
            tableOrders.getCompletedOrders().remove(orderId);
            if (changeStatus.equals(Order.OrderStatus.CONFIRMED)) {
                tableOrders.getConfirmedOrders().add(orderId);
            }
        } else if (currentStatus.equals(Order.OrderStatus.PENDING)) {
            tableOrders.getPendingOrders().remove(orderId);
            if (changeStatus.equals(Order.OrderStatus.COMPLETED)) {
                tableOrders.getCompletedOrders().add(orderId);
            } else if (changeStatus.equals(Order.OrderStatus.CONFIRMED)) {
                tableOrders.getConfirmedOrders().add(orderId);
            }
        }

        saveTableOrders(tableId, tableOrders);

        return tableOrders;
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

            TableOrderInfo tableOrderInfo = getTableOrders(tableId);
            if (tableOrderInfo != null) {
                tableOrderInfos.add(tableOrderInfo);
            }
        }
        return tableOrderInfos;
    }


    public TableOrderInfo getTableOrders(Integer tableId) {
        Object object = redisTemplate.opsForValue().get("tableOrders:" + tableId);
        if (object == null) {
            return new TableOrderInfo(
                    tableId,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }
        return jacksonObjectMapper.convertValue(object, TableOrderInfo.class);
    }

    public void deleteOrderIdFromTable(Integer tableId, Integer orderId, Order.OrderStatus orderStatus) {
        TableOrderInfo tableOrders = getTableOrders(tableId);
        if (orderStatus.equals(Order.OrderStatus.COMPLETED)) {
            tableOrders.getCompletedOrders().remove(orderId);
        } else if (orderStatus.equals(Order.OrderStatus.PENDING)) {
            tableOrders.getPendingOrders().remove(orderId);
        } else if (orderStatus.equals(Order.OrderStatus.CONFIRMED)) {
            tableOrders.getConfirmedOrders().remove(orderId);
        }
        redisTemplate.opsForValue().set("tableOrders:" + tableId, tableOrders);
    }


    public void deleteTableOrders(Integer tableId) {
        redisTemplate.delete("tableOrders:" + tableId);
    }

}
