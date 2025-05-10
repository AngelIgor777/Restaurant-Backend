package org.test.restaurant_service.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.test.restaurant_service.util.JwtAlgorithmUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String COOKIE_NAME = "ACCESS_TOKEN";


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
                DecodedJWT decodedJWT = tryVerifyAllTokens(token);
                setUpAuthentication(decodedJWT);
                refreshAccessCookie(response, token, false, "Strict");
            } catch (JWTVerificationException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if (COOKIE_NAME.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        // 2) fallback: Authorization Bearer (чтобы не упало у старых клиентов)
        var h = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (h != null && h.startsWith(BEARER_PREFIX)) ? h.substring(BEARER_PREFIX.length()) : null;
    }

    private DecodedJWT verifyAccessToken(String token) {
        return JWT.require(JwtAlgorithmUtil.getAccessAlgorithm())
                .build()
                .verify(token);
    }

    private DecodedJWT verifyDisposableToken(String token) {
        return JWT.require(JwtAlgorithmUtil.getAdminDisposableAlgorithm())
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


    private DecodedJWT tryVerifyAllTokens(String token) {
        try {
            return verifyAccessToken(token);
        } catch (JWTVerificationException e) {
        }

        try {
            return verifyDisposableToken(token);
        } catch (JWTVerificationException e) {
        }

        throw new JWTVerificationException("Invalid token");
    }

    public static void refreshAccessCookie(HttpServletResponse resp, String jwt,
                                           boolean secure, String sameSite) {
        ResponseCookie c = ResponseCookie.from("ACCESS_TOKEN", jwt)
                .httpOnly(true).secure(secure)
                .sameSite(sameSite).path("/")
                .maxAge(Duration.ofMinutes(30))
                .build();
        resp.addHeader(HttpHeaders.SET_COOKIE, c.toString());
    }
}
