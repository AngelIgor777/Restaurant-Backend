package org.test.restaurant_service.dto.request.order;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.rabbitmq.producer.RabbitMQJsonProducer;

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
    protected List<OrderProductRequestDTO> orderProductRequestDTO;

    @NotNull(message = "Payment method is required")
    protected Order.PaymentMethod paymentMethod;


    protected boolean orderInRestaurant;

    protected TableRequestDTO tableRequestDTO;

    protected boolean existDiscountCodes;

    protected String productDiscountCode;

    protected String globalDiscountCode;

    protected String phoneNumber;

    protected boolean userRegistered;

    protected UUID userUUID;

    protected AddressRequestDTO addressRequestDTO;

    protected String otp;
}
