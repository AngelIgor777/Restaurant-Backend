package org.test.restaurant_service.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.test.restaurant_service.entity.Order;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductWithPayloadAndPrintRequestDto extends OrderProductWithPayloadRequestDto{
    protected ProductsForPrintRequest productsIdForPrint;
    protected Order.OrderStatus orderStatus;

}
