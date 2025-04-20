package org.test.restaurant_service.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.dto.response.TableOrderScoreResponseDTO;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.TableOrderScore;
import org.test.restaurant_service.repository.TableOrderScoreRepository;
import org.test.restaurant_service.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OrderTableScoreService {
    private final OrderService orderService;
    private final TableOrderScoreRepository scoreRepository;


    public TableOrderScoreResponseDTO getTableOrderScore(UUID sessionUUID, Integer tableId) {
        List<TableOrderScore> scores = scoreRepository.findAllBySessionUUIDAndTable_Id(sessionUUID, tableId);

        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);

        List<Integer> orderIds = scores.stream()
                .map(TableOrderScore::getOrder)
                .peek(order -> totalPrice.updateAndGet(current -> current.add(order.getTotalPrice())))
                .map(Order::getId)
                .toList();
        List<OrderProductResponseWithPayloadDto> orderDtos = orderService.searchOrdersWithPayloadDtoById(orderIds);

        return TableOrderScoreResponseDTO.builder()
                .tableId(tableId)
                .orders(orderDtos)
                .totalPrice(totalPrice.get())
                .build();
    }
}
