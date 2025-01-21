package org.test.restaurant_service.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.test.restaurant_service.entity.Order;

import javax.validation.constraints.*;
import java.util.List;

/**
 * DTO for handling order product requests with payload details.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductRequestDtoWithPayloadDto {

    /**
     * List of product requests within an order.
     * Each request contains product details and quantity.
     */
    @NotNull(message = "Order products cannot be null")
    @Size(min = 1, message = "At least one product must be included")
    private List<OrderProductRequestDTO> orderProductRequestDTO;

    /**
     * Payment method for the order (e.g., CARD, CASH, etc.).
     */
    @NotNull(message = "Payment method is required")
    private Order.PaymentMethod paymentMethod;


    private boolean isOrderInRestaurant;
    /**
     * Table information for dine-in orders.
     * Optional for delivery orders.
     */
    private TableRequestDTO tableRequestDTO;

    /**
     * Indicates if discount codes exist in the request.
     */
    private boolean existDiscountCodes;

    /**
     * Product-specific discount code, if applicable.
     */
    @Pattern(regexp = "^[A-Z0-9_]{5,20}$", message = "Invalid product discount code format")
    private String productDiscountCode;

    /**
     * General discount code for the entire order, if applicable.
     */
    @Pattern(regexp = "^[A-Z0-9_]{5,20}$", message = "Invalid discount code format")
    private String globalDiscountCode;

    private AddressRequestDTO addressRequestDTO;


}
