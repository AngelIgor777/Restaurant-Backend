package org.test.restaurant_service.dto.response;

import lombok.Data;

@Data
public class TelegramUserDTO {
    private Integer id;
    private Long chatId;
    private String username;
    private String photoUrl;
    private String firstname;
}
