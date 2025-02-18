package org.test.restaurant_service.dto.response;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductHistoryResponseDTO extends ProductResponseDTO {
    private String photoUrl;
    private Integer productHistoryId;
    private LocalDateTime changedAt;
}
