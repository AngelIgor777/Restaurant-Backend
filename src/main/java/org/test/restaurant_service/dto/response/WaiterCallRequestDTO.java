package org.test.restaurant_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Builder
@Data
public class WaiterCallRequestDTO {
    private Integer tableNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime requestTime;
    private TelegramUserDTO telegramUser;
}
