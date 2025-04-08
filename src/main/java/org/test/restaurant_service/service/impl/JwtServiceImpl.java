package org.test.restaurant_service.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.admin.JwtResponse;
import org.test.restaurant_service.entity.Admin;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.util.JwtAlgorithmUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final HttpServletRequest request;


    public String generateUserAccessToken(Long chatId, List<String> roles) {
        Algorithm algorithm = JwtAlgorithmUtil.getAccessAlgorithm();

        long ACCESS_TOKEN_EXPIRATION_TIME = (30 * 24 * 60 * 60 * 1000L) * 6;
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
        return JWT.require(JwtAlgorithmUtil.getAccessAlgorithm())
                .build()
                .verify(accessToken)
                .getClaim("roles").asList(String.class);
    }

    @Override
    public List<String> getRolesFromDisposableToken(String disposableToken) {
        return JWT.require(JwtAlgorithmUtil.getAdminDisposableAlgorithm())
                .build()
                .verify(disposableToken)
                .getClaim("roles").asList(String.class);
    }

    public JwtResponse generateJwtResponseForAdmin(Admin admin, User user) {
        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getRoleName().name())
                .toList();
        Algorithm algorithm = JwtAlgorithmUtil.getAdminDisposableAlgorithm();
        String token = JWT.create()
                .withSubject(admin.getLogin())
                .withClaim("roles", roles)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 12))
                .sign(algorithm);
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setDisposableToken(token);
        jwtResponse.setUserUUID(user.getUuid());
        return jwtResponse;
    }

}
