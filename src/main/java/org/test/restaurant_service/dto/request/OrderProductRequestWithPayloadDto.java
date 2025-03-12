package org.test.restaurant_service.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.test.restaurant_service.entity.Order;

import javax.validation.constraints.*;
import java.util.List;
import java.util.UUID;


/**
 * DTO for handling order product requests with payload details.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductRequestWithPayloadDto {

    @NotNull(message = "Order products cannot be null")
    @Size(min = 1, message = "At least one product must be included")
    private List<OrderProductRequestDTO> orderProductRequestDTO;

    @NotNull(message = "Payment method is required")
    private Order.PaymentMethod paymentMethod;

    private boolean orderInRestaurant;

    private TableRequestDTO tableRequestDTO;

    private boolean existDiscountCodes;

    private String productDiscountCode;

    private String phoneNumber;

    private String globalDiscountCode;

    private boolean userRegistered;

    private UUID userUUID;

    private AddressRequestDTO addressRequestDTO;

    private String otp;

}
