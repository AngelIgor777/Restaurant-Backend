package org.test.restaurant_service.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class UserInfoResponse {
    private UUID uuid;
    private AddressResponseDTO addressResponseDTO;
    private Long chatId;
    private String username;
    private String firstname;
}
