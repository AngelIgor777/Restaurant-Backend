package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.OrderRequestDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.entity.Order;

import java.util.List;

public interface OrderService {
    OrderResponseDTO create(OrderRequestDTO requestDTO);

    Order create(Order order);

    List<Order> createAll(List<Order> orders);

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(Integer id);

    OrderResponseDTO update(Integer id, OrderRequestDTO requestDTO);

    void delete(Integer id);

    List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto();

    OrderProductResponseWithPayloadDto getOrderProductResponseWithPayloadDto(Integer id);
}
