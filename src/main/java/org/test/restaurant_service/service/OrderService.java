package org.test.restaurant_service.service;

import org.springframework.data.domain.Pageable;
import org.test.restaurant_service.dto.request.OrderRequestDTO;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;
import org.test.restaurant_service.dto.request.table.TableOrdersPriceInfo;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.dto.response.OrdersStatesCount;
import org.test.restaurant_service.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    boolean existsByOtp(String otp);

    Order create(Order order);

    List<Order> createAll(List<Order> orders);

    List<OrderResponseDTO> getAllOrders();

    Order getOrderById(Integer id);

    void delete(Integer id);

    List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto(Order.OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto(Order.OrderStatus status, LocalDateTime from, LocalDateTime to);

    OrderProductResponseWithPayloadDto getOrderProductResponseWithPayloadDto(Integer id);

    List<Order> getAllOrdersByPeriod(LocalDateTime from, LocalDateTime to);

    void completeOrder(Integer orderId);

    void confirmOrder(Integer orderId);

    List<OrderProductResponseWithPayloadDto> getAllUserOrdersProductResponseWithPayloadDto(UUID userUUID, Pageable pageable);

    Integer getCountOrdersByUserChatId(Long chatId);

    void deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus status, LocalDateTime from, LocalDateTime to);

    OrderProductResponseWithPayloadDto searchOrderProductResponseWithPayloadDtoByValidationCode(String query);

    List<OrderProductResponseWithPayloadDto> searchOrdersWithPayloadDtoById(List<Integer> ids);

    OrdersStatesCount getOrdersStatesCount();

    TableOrdersPriceInfo countPriceForTable(Integer tableId);

    void setTableMetaData(TableOrderInfo tableOrderInfo);
}
