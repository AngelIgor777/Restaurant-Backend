package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.OrderRequestDTO;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.Table;
import org.test.restaurant_service.mapper.OrderMapper;
import org.test.restaurant_service.repository.OrderRepository;
import org.test.restaurant_service.repository.TableRepository;
import org.test.restaurant_service.service.OrderService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
        Table table = tableRepository.findById(requestDTO.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id " + requestDTO.getTableId()));
        Order order = orderMapper.toEntity(requestDTO);
        order.setTable(table);
        order = orderRepository.save(order);
        return orderMapper.toResponseDTO(order);
    }

    @Override
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> createAll(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));
        return orderMapper.toResponseDTO(order);
    }

    @Override
    public OrderResponseDTO update(Integer id, OrderRequestDTO requestDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));
        orderMapper.updateEntityFromRequestDTO(requestDTO, order);
        if (requestDTO.getTableId() != null) {
            Table table = tableRepository.findById(requestDTO.getTableId())
                    .orElseThrow(() -> new EntityNotFoundException("Table not found with id " + requestDTO.getTableId()));
            order.setTable(table);
        }
        order = orderRepository.save(order);
        return orderMapper.toResponseDTO(order);
    }

    @Override
    public void delete(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id " + id);
        }
        orderRepository.deleteById(id);
    }
}
