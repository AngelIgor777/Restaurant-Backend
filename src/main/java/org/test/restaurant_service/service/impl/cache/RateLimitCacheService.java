package org.test.restaurant_service.service.impl.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
public class RateLimitCacheService {
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "rate-limit:";

    public RateLimitCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long increment(String keyKey, Duration window) {
        String key = PREFIX + keyKey;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, window);
        }
        return count == null ? 0L : count;
    }

    public void reset(String keyKey) {
        redisTemplate.delete(PREFIX + keyKey);
    }
}
