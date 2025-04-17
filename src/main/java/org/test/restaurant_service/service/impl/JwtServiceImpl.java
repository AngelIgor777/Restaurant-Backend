package org.test.restaurant_service.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.Role;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.entity.Admin;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.util.JwtAlgorithmUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final UserService userService;

    private final HttpServletRequest request;

    private static long ACCESS_TOKEN_EXPIRATION_TIME = (30 * 24 * 60 * 60 * 1000L) * 6;

    public String generateUserAccessToken(Long chatId, List<String> roles) {
        Algorithm algorithm = JwtAlgorithmUtil.getAccessAlgorithm();

        return JWT.create()
                .withSubject(chatId.toString())
                .withClaim("roles", roles)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);
    }

    @Override
    public JwtResponse generateUserAccessToken(UUID userUUID) {
        User user = userService.findByUUID(userUUID);
        List<String> userRoles = user.getRoles()
                .stream()
                .map(role -> role.getRoleName().name())
                .toList();
        Algorithm algorithm = JwtAlgorithmUtil.getAccessAlgorithm();
        String accessToken = JWT.create()
                .withSubject(user.getTelegramUserEntity().getChatId().toString())
                .withClaim("roles", userRoles)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .userUUID(userUUID).build();
    }

    @Override
    public Long getChatId(String accessToken) {
        String subject = JWT.require(JwtAlgorithmUtil.getAccessAlgorithm())
                .build()
                .verify(accessToken)
                .getSubject();
        return Long.parseLong(subject);
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

    public org.test.restaurant_service.dto.response.admin.JwtResponse generateJwtResponseForAdmin(Admin admin, User user) {
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
        org.test.restaurant_service.dto.response.admin.JwtResponse jwtResponse = new org.test.restaurant_service.dto.response.admin.JwtResponse();
        jwtResponse.setDisposableToken(token);
        jwtResponse.setUserUUID(user.getUuid());
        return jwtResponse;
    }

}
