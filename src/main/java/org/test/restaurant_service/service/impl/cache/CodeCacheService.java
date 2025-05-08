package org.test.restaurant_service.service.impl.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.Codes;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CodeCacheService {
    private static final String CODE_KEY = "CURR_ORD_CODE";
    private static final String ACTIVE_USER = "ACTIVE_USERS";
    private static final String INCORRECT_INP = "INCORRECT_INP";
    private static final String BLACKLIST = "BLACKLIST";

    private final RedisTemplate<String, Object> redis;
    private final ObjectMapper objectMapper;

    public Codes rotateCodes() {
        int trueCode = randomCode();
        int falseCode1 = randomCode();
        int falseCode2 = randomCode();
        while ((falseCode1 == falseCode2) || (trueCode == falseCode1) || (trueCode == falseCode2)) {
            falseCode1 = randomCode();
            falseCode2 = randomCode();
        }
        Codes value = new Codes(trueCode, falseCode1, falseCode2);
        redis.opsForValue().set(CODE_KEY, value);
        return value;
    }

    public Codes getOrderCode() {
        Object raw = redis.opsForValue().get(CODE_KEY);
        Codes value = objectMapper.convertValue(raw, Codes.class);
        return value == null ? rotateCodes() : value;
    }

    public boolean isValidCode(Integer code) {
        return getOrderCode().getTrueCode() == code;
    }


    // for active user can put only chatId in telegram or sessionUUID in website and then every order is must check it
    public void activateUser(Object value) {
        redis.opsForSet().add(ACTIVE_USER, value);
        redis.expire(ACTIVE_USER, Duration.ofMinutes(30));
    }

    // for active user can put only chatId in telegram or sessionUUID
    public boolean isUserActive(Object value) {
        return Boolean.TRUE.equals(redis.opsForSet().isMember(ACTIVE_USER, value));
    }

    // for active user can put only chatId in telegram or sessionUUID
    public int incorrectInput(Object value) {
        Object object = redis.opsForValue().get(INCORRECT_INP + value);
        Integer countIncorrectInputs = objectMapper.convertValue(object, Integer.class);

        int totalErrors = 0;
        if (countIncorrectInputs == null) {
            totalErrors = 1;
        } else {
            totalErrors = countIncorrectInputs + 1;
        }
        redis.opsForValue().set(INCORRECT_INP + value, totalErrors);
        return totalErrors;
    }

    public void clearIncorrectInput(Object value) {
        redis.delete(INCORRECT_INP + value);
    }


    public int randomCode() {
        return ThreadLocalRandom.current().nextInt(10, 100);
    }


    public void blacklistUser(Object value) {
        redis.opsForSet().add(BLACKLIST, value);
        redis.expire(BLACKLIST, Duration.ofHours(1));
    }

    public boolean isBlacklisted(Object value) {
        return Boolean.TRUE.equals(redis.opsForSet().isMember(BLACKLIST, value));
    }

}
