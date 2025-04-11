package org.test.restaurant_service.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.test.restaurant_service.entity.Order;

import javax.validation.constraints.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductWithPayloadRequestDto {

    @NotNull(message = "Order products cannot be null")
    @Size(min = 1, message = "At least one product must be included")
    private List<OrderProductRequestDTO> orderProductRequestDTO;

    @NotNull(message = "Payment method is required")
    private Order.PaymentMethod paymentMethod;

    private Order.OrderStatus orderStatus;

    private boolean orderInRestaurant;

    private TableRequestDTO tableRequestDTO;

    private boolean existDiscountCodes;

    private String productDiscountCode;

    private String globalDiscountCode;

    private String phoneNumber;

    private boolean userRegistered;

    private UUID userUUID;

    private AddressRequestDTO addressRequestDTO;

    private String otp;

}
