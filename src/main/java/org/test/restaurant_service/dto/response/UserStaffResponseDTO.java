package org.test.restaurant_service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
public class UserStaffResponseDTO {
    private UUID uuid;
    private Long chatId;
    private String username;
    private String firstname;
    private String photoUrl;
    private List<String> roles;
}
