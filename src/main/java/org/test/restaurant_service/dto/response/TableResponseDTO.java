package org.test.restaurant_service.dto.response;

import lombok.Data;

@Data
public class TableResponseDTO {
    private Integer id;
    private Integer number;
    private boolean isOpen = false;
}
