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

    private static final String DISPOSABLE = "DISPOSABLE_TOKEN";
    private static final String ACCESS = "ACCESS_TOKEN";
    private static final String ACTIVATION = "ACTIVATION_TOKEN";


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return List.of(
                "/api/v1/otp"
        ).contains(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse resp,
            FilterChain chain
    ) throws ServletException, IOException {

        String uri = req.getRequestURI();
        String tokenCookieName;
        if (uri.startsWith("/api/v1/statistics") || uri.startsWith("/api/v1/exportOrders")) {
            tokenCookieName = DISPOSABLE;
        } else if (uri.endsWith("call-waiter") || uri.startsWith("/api/v1/order-products/bulk")) {
            tokenCookieName = ACTIVATION;
        } else {
            tokenCookieName = ACCESS;
        }

        String token = extractCookie(req, tokenCookieName);
        if (token != null) {
            try {
                DecodedJWT decoded;
                if (tokenCookieName.equals(DISPOSABLE)) {
                    decoded = verifyDisposableToken(token);
                } else if (tokenCookieName.equals(ACTIVATION)) {
                    decoded = verifyActivationToken(token);
                } else {
                    decoded = verifyAccessToken(token);
                }

                setUpAuthentication(decoded, tokenCookieName);

                refreshCookie(resp, tokenCookieName, token);
            } catch (JWTVerificationException e) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(req, resp);
    }

    private String extractCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (var c : req.getCookies()) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
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

    private DecodedJWT verifyActivationToken(String token) {
        return JWT.require(JwtAlgorithmUtil.getUserActivationAlgorithm())
                .build()
                .verify(token);
    }

    private void setUpAuthentication(DecodedJWT decodedJWT, String tokenType) {
        String chatId = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        if (chatId != null) {
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if (tokenType.equals(DISPOSABLE)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_DISPOSABLE"));
            } else if (tokenType.equals(ACTIVATION)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ACTIVATION"));
            }


            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            chatId,
                            null,
                            authorities
                    );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    private void refreshCookie(HttpServletResponse resp,
                               String name,
                               String token) {
        // однообразно обновляем maxAge, sameSite и т.д.
        ResponseCookie cookie = ResponseCookie.from(name, token)
                .httpOnly(true).secure(false)
                .sameSite("Strict").path("/")
                .maxAge(Duration.ofHours(
                        name.equals(DISPOSABLE) ? 24 : 1
                ))
                .build();
        resp.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
