package org.test.restaurant_service.service.impl.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadRequestDto;

import java.time.Duration;

@Service
public class OrderCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper jacksonObjectMapper;

    public OrderCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper jacksonObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public void saveOrder(Long chatId, OrderProductWithPayloadRequestDto orderDto) {
        redisTemplate.opsForValue().set("order:" + chatId, orderDto, Duration.ofMinutes(5));
    }

    public OrderProductWithPayloadRequestDto getOrder(Long chatId) {
        Object object = redisTemplate.opsForValue().get("order:" + chatId);
        return jacksonObjectMapper.convertValue(object, OrderProductWithPayloadRequestDto.class);
    }

    public void deleteOrder(Long chatId) {
        redisTemplate.delete("order:" + chatId);
    }
}
