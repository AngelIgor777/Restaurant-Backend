// src/main/java/org/test/restaurant_service/security/filters/RedisIpRateLimitFilter.java

package org.test.restaurant_service.security.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.test.restaurant_service.service.impl.cache.RateLimitCacheService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisIpRateLimitFilter extends OncePerRequestFilter {

    private final RateLimitCacheService rateLimitCacheService;
    private static final int REQUEST_LIMIT = 7;
    private static final Duration WINDOW = Duration.ofSeconds(1);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        long count = rateLimitCacheService.increment(ip, WINDOW);

        if (count > REQUEST_LIMIT) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
