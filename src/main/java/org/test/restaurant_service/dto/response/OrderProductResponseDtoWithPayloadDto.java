package org.test.restaurant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductResponseDtoWithPayloadDto {
    private OrderResponseDTO payload;
    private boolean orderInRestaurant;
    private boolean existDiscountCodes;
    private String productDiscountCode;
    private String globalDiscountCode;
    private AddressResponseDTO addressResponseDTO;
}
