package org.test.restaurant_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAndPhotosRequest {

    private ProductRequestDTO productRequestDTO;
    private List<PhotoRequestDTO> photoRequestDTO;
}
