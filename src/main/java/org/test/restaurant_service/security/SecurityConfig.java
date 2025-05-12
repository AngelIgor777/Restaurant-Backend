package org.test.restaurant_service.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.test.restaurant_service.security.filters.JwtAuthenticationFilter;
import org.test.restaurant_service.security.filters.RedisIpRateLimitFilter;
import org.test.restaurant_service.security.service.CustomUserDetailsService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.service.impl.cache.RateLimitCacheService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RedisIpRateLimitFilter rateLimitFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/api/v1/otp/**",
                        "/api/v1/tables/**",
                        "/api/v1/products/**",
                        "/api/v1/product-types/**",
                        "/api/v1/photos/**",
                        "/api/v1/order-products/**",
                        "/api/v1/discounts/**",
                        "/api/v1/scheduler/**",
                        "/api/v1/orders/**",
                        "/api/v1/users/**",
                        "/api/v1/addresses/**",
                        "/api/v1/productHistory/**",
                        "/api/v1/product-discounts/**",
                        "/api/v1/admin/**",
                        "/api/v1/product-translations/**",
                        "/api/v1/product-type-translations/**",
                        "/api/v1/connection/**",
                        "/api/v1/tg/**",
                        "/api/v1/auth/**",
                        "/api/v1/statistics/**",
                        "/api/v1/exportOrders/**",
                        "/api/v1/jwt/**",
                        "/api/v1/waiter-calls/**",
                        "/api/v1/languages/**",
                        "/api/v1/ui-translations/**",
                        "/actuator/**",
                        "/api/v1/shared-buckets/**",
                        "/ws-open-tables/**",
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                // 1) сначала rate-limit — до UsernamePasswordAuthenticationFilter
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                // 2) затем JWT-аутентификация — тоже до UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService())
                .and()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userService);
    }

}