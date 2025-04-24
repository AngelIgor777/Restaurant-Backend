package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseDTO;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.OrderProduct;

import java.util.List;

public interface OrderProductService {

    List<OrderProduct> getOrderProductsByOrderId(Integer orderId);

    OrderProductResponseDTO update(Integer id, OrderProductRequestDTO requestDTO, Integer orderId);

    void createAll(List<OrderProduct> orderProducts);

    void delete(Integer id);
}
