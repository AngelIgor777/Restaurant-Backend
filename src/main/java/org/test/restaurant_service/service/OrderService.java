package org.test.restaurant_service.service;

import org.springframework.data.domain.Pageable;
import org.test.restaurant_service.dto.request.OrderRequestDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDTO create(OrderRequestDTO requestDTO);

    boolean existsByOtp(String otp);

    Order create(Order order);

    List<Order> createAll(List<Order> orders);

    List<OrderResponseDTO> getAllOrders();

    Order getOrderById(Integer id);

    OrderResponseDTO update(Integer id, OrderRequestDTO requestDTO);

    void delete(Integer id);

    List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto();

    OrderProductResponseWithPayloadDto getOrderProductResponseWithPayloadDto(Integer id);

    List<Order> getAllOrdersByPeriod(LocalDateTime from, LocalDateTime to);

    void completeOrder(Integer orderId);

    void confirmOrder(Integer orderId);

    List<OrderProductResponseWithPayloadDto> getAllUserOrdersProductResponseWithPayloadDto(UUID userUUID, Pageable pageable);

    Integer getCountOrdersByUserChatId(Long chatId);

    void deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus status, LocalDateTime from, LocalDateTime to);

    OrderProductResponseWithPayloadDto searchOrderProductResponseWithPayloadDtoByValidationCode(String query);
}
