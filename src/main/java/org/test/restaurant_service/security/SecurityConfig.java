package org.test.restaurant_service.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.test.restaurant_service.security.filters.JwtAuthenticationFilter;
import org.test.restaurant_service.security.service.CustomUserDetailsService;
import org.test.restaurant_service.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final UserService userService;


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
                        "/ws-orders/**",
                        "/topic/orders/**",
                        "/api/v1/users/**",
                        "/api/v1/addresses/**",
                        "/api/v1/productHistory/**",
                        "/api/v1/product-discounts/**",
                        "/api/v1/admin/**",
                        "/api/v1/product-translations/**",
                        "/api/v1/product-type-translations/**",
                        "/api/v1/connection/**",
                        "/api/v1/tg/**",
                        "/actuator/**",
                        "/api/v1/shared-buckets/**",
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class); // Проверка JWT

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

    @Bean
    public JwtAuthenticationFilter authFilter() {
        return new JwtAuthenticationFilter();
    }

}