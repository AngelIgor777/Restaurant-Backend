package org.test.restaurant_service.dto.request.order;

import lombok.Data;

import java.util.List;

@Data
public class ProductsForPrintRequest {
    List<Integer> products;
}
