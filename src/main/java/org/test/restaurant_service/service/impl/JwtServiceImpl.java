package org.test.restaurant_service.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Role;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.util.JwtAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final HttpServletRequest request;


    private static final long ACCESS_TOKEN_EXPIRATION_TIME = (30 * 24 * 60 * 60 * 1000L) * 6;

    public String generateUserAccessToken(Long chatId, List<String> roles) {
        Algorithm algorithm = JwtAlgorithm.getAccessAlgorithm();

        return JWT.create()
                .withSubject(chatId.toString())
                .withClaim("roles", roles)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);
    }

    @Override
    public String extractToken() {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new IllegalArgumentException("JWT not exist");
    }

    @Override
    public List<String> getRoles(String accessToken) {
        return JWT.require(JwtAlgorithm.getAccessAlgorithm())
                .build()
                .verify(accessToken)
                .getClaim("roles").asList(String.class);
    }
}
