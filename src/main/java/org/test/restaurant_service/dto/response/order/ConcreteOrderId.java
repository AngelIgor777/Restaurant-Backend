package org.test.restaurant_service.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConcreteOrderId implements OrderId {
    private Integer id;
}
