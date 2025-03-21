package org.test.restaurant_service.security.filters;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@Component
public class RedisIpRateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private static final int REQUEST_LIMIT = 10;
    private static final Duration WINDOW = Duration.ofSeconds(1);

    public RedisIpRateLimitFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String key = "rate-limit:" + ip;

        Long requests = redisTemplate.opsForValue().increment(key);
        if (requests == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        if (requests > REQUEST_LIMIT) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests from this IP");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
