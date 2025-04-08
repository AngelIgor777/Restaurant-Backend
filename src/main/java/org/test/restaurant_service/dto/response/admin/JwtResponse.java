package org.test.restaurant_service.dto.response.admin;

import lombok.Data;

import java.util.UUID;

@Data
public class JwtResponse {
    private String disposableToken;
    private UUID userUUID;
}
