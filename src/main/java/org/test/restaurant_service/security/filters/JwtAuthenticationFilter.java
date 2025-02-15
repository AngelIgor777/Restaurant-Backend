package org.test.restaurant_service.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return List.of(
                "/api/v1/otp"
        ).contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null) {
            try {
                DecodedJWT decodedJWT = verifyToken(token);
                setUpAuthentication(decodedJWT);
            } catch (JWTVerificationException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private DecodedJWT verifyToken(String token) {
        return JWT.require(org.test.restaurant_service.util.JwtAlgorithm.getAccessAlgorithm())
                .build()
                .verify(token);
    }

    private void setUpAuthentication(DecodedJWT decodedJWT) {
        String chatId = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        if (chatId != null) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    chatId,
                    null,
                    roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
