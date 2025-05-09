package org.test.restaurant_service.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class TableResponseDTO {
    private Integer id;
    private Integer number;
    private UUID sessionUUID;
    private boolean isOpen = false;
}
