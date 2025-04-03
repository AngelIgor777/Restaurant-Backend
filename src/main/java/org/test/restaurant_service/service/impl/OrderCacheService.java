package org.test.restaurant_service.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.OrderProductRequestWithPayloadDto;

import java.time.Duration;

@Service
public class OrderCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public OrderCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOrder(Long chatId, OrderProductRequestWithPayloadDto orderDto) {
        redisTemplate.opsForValue().set("order:" + chatId, orderDto, Duration.ofMinutes(5)); // Save for 5 min
    }

    public OrderProductRequestWithPayloadDto getOrder(Long chatId) {
        return (OrderProductRequestWithPayloadDto) redisTemplate.opsForValue().get("order:" + chatId);
    }

    public void deleteOrder(Long chatId) {
        redisTemplate.delete("order:" + chatId);
    }
}
