package org.test.restaurant_service.service.impl.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.WaiterCallRequestDTO;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WaiterCallCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "waiterCall:";
    private final ObjectMapper jacksonObjectMapper;


    public void saveWaiterCall(WaiterCallRequestDTO dto) {
        String key = PREFIX + dto.getTableNumber();
        redisTemplate.opsForValue().set(key, dto, Duration.ofMinutes(15));
    }

    public WaiterCallRequestDTO getWaiterCallByTable(Integer tableNumber) {
        return jacksonObjectMapper.convertValue(redisTemplate.opsForValue().get(PREFIX + tableNumber), WaiterCallRequestDTO.class);
    }

    public void deleteWaiterCall(Integer tableNumber) {
        redisTemplate.delete(PREFIX + tableNumber);
    }

    public List<WaiterCallRequestDTO> getAllWaiterCalls() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }

        List<WaiterCallRequestDTO> calls = new ArrayList<>();
        for (String key : keys) {
            Object value = redisTemplate.opsForValue().get(key);
            WaiterCallRequestDTO waiterCallRequestDTO = jacksonObjectMapper.convertValue(value, WaiterCallRequestDTO.class);
            calls.add(waiterCallRequestDTO);
        }
        return calls;
    }
}
