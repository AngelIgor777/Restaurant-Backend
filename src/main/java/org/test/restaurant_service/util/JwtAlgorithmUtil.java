package org.test.restaurant_service.util;


import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAlgorithmUtil {

    public static Algorithm getRefreshAlgorithm() {
        return Algorithm.HMAC256(KeyUtil.getRefreshSecret().getBytes());
    }

    public static Algorithm getAdminDisposableAlgorithm() {
        return Algorithm.HMAC256(KeyUtil.getDisposableAdminKey().getBytes());
    }

    public static Algorithm getAccessAlgorithm() {
        return Algorithm.HMAC256(KeyUtil.getAccessSecret().getBytes());
    }

}
