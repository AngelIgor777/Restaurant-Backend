package org.test.restaurant_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class JwtResponse {
    private String token;
    private UUID userUUID;
}
