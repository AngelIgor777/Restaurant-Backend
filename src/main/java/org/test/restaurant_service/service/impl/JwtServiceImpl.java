package org.test.restaurant_service.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.util.JwtAlgorithm;

import java.util.Date;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = (30 * 24 * 60 * 60 * 1000L) * 6;

    public String generateUserAccessToken(Long chatId, List<String> roles) {
        Algorithm algorithm = JwtAlgorithm.getAccessAlgorithm();

        return JWT.create()
                .withSubject(chatId.toString())
                .withClaim("roles", roles)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);
    }
}
