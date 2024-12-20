package org.test.restaurant_service.dto.response;

import lombok.Data;

@Data
public class PhotoResponseDTO {
    private Integer id;
    private Integer productId;
    private String url;
}