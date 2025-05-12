package org.test.restaurant_service.service.impl.cache;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.feats.Features;

import java.time.Duration;


@Service
public class FeatureCacheService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "feature:";

    public FeatureCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveFeature(Features feature) {
        String key = PREFIX + feature.name();
        redisTemplate.opsForValue().set(key, feature.name());
    }

    public Features getFeature(Features feature) {
        String key = PREFIX + feature.name();
        String value = redisTemplate.opsForValue().get(key);
        return (value != null) ? Features.valueOf(value) : null;
    }

    public void deleteFeature(Features feature) {
        String key = PREFIX + feature.name();
        redisTemplate.delete(key);
    }
}
