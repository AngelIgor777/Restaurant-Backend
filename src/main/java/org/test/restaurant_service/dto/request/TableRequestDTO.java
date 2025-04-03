package org.test.restaurant_service.dto.request;

import lombok.Data;

@Data
public class TableRequestDTO {
    private Integer number;

    public TableRequestDTO(Integer number) {
        this.number = number;
    }

    public TableRequestDTO() {
    }
}
