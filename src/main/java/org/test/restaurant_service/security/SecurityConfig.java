package org.test.restaurant_service.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.test.restaurant_service.security.filters.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and() // Включаем поддержку CORS
                .csrf().disable() // Отключаем CSRF
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
                        "/api/v1/statistics/**",
                        "/actuator/**",
                        "/images/**"
                ).permitAll() // Разрешаем доступ без аутентификации
                .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class); // Добавляем фильтр для JWT
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }




}