package org.test.restaurant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductResponseWithPayloadDto {
    private OrderResponseDTO orderResponseDTO;
    private boolean orderInRestaurant;
    private boolean existDiscountCodes;
    private String productDiscountCode;
    private String globalDiscountCode;
    private AddressResponseDTO addressResponseDTO;
    private TableResponseDTO tableResponseDTO;
    private UUID userUUID;
    private String phoneNumber;
    private String otp;
}
