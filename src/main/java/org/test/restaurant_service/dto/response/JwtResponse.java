package org.test.restaurant_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtResponse {
    private String accessToken;
    private Integer userId;
}
