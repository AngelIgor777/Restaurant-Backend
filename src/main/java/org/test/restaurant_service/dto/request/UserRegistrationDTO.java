package org.test.restaurant_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {
    private Long chatId;
    private User user;
}
