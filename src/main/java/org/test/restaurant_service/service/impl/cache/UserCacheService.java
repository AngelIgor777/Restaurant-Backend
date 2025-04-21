package org.test.restaurant_service.service.impl.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserCacheService {
    private final StringRedisTemplate redisTemplate;

    public UserCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveUserState(Long chatId, String state) {
        redisTemplate.opsForValue().set("u_st:" + chatId, state, Duration.ofMinutes(5));
    }

    public String getUserState(Long chatId) {
        return redisTemplate.opsForValue().get("u_st:" + chatId);
    }

    public void removeUserState(Long chatId) {
        redisTemplate.delete("u_st:" + chatId);
    }
}
