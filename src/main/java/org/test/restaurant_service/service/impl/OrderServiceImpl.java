package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.OrderRequestDTO;
import org.test.restaurant_service.dto.response.*;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.mapper.AddressMapperImpl;
import org.test.restaurant_service.mapper.OrderMapper;
import org.test.restaurant_service.mapper.ProductMapperImpl;
import org.test.restaurant_service.mapper.TableMapperImpl;
import org.test.restaurant_service.repository.OrderDiscountRepository;
import org.test.restaurant_service.repository.OrderRepository;
import org.test.restaurant_service.repository.TableRepository;
import org.test.restaurant_service.service.OrderDiscountService;
import org.test.restaurant_service.service.OrderProductService;
import org.test.restaurant_service.service.OrderService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final OrderMapper orderMapper;
    private final OrderDiscountService orderDiscountService;
    private final OrderProductService orderProductService;
    private final OrderDiscountRepository orderDiscountRepository;
    private final OrderDiscountServiceImpl orderDiscountServiceImpl;
    private final AddressMapperImpl addressMapperImpl;
    private final ProductServiceImpl productServiceImpl;
    private final ProductMapperImpl productMapperImpl;
    private final TableMapperImpl tableMapperImpl;

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
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));

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


    //todo finish it
    @Transactional(readOnly = true)
    @Override
    public List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto() {
        List<OrderProductResponseWithPayloadDto> list = orderRepository.findAll()
                .stream()
                .map(order -> {
                    OrderProductResponseWithPayloadDto response = getOrderProductResponseWithPayloadDto(order);
                    return response;
                })
                .toList();
        return list;
    }

    @Override
    public OrderProductResponseWithPayloadDto getOrderProductResponseWithPayloadDto(Integer id) {
        return getOrderProductResponseWithPayloadDto(orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id)));
    }

    @Override
    public List<Order> getAllOrdersByPeriod(LocalDateTime from, LocalDateTime to) {
        return orderRepository.findAllByCreatedAtBetween(from, to);
    }

    @Override
    public void completeOrder(Integer orderId) {
        Order orderById = getOrderById(orderId);
        orderById.setStatus(Order.OrderStatus.COMPLETED);
    }

    @Override
    public void confirmOrder(Integer orderId) {
        Order orderById = getOrderById(orderId);
        orderById.setStatus(Order.OrderStatus.CONFIRMED);
    }

    private OrderProductResponseWithPayloadDto getOrderProductResponseWithPayloadDto(Order order) {
        OrderProductResponseWithPayloadDto response = new OrderProductResponseWithPayloadDto();
        OrderResponseDTO orderResponseDTO = orderMapper.toResponseDTO(order);
        AtomicReference<LocalTime> totalCookingTime = new AtomicReference<>(LocalTime.MIN);

        List<ProductResponseDTO> productResponseDTOS = orderProductService.getOrderProductsByOrderId(order.getId())
                .stream()
                .map(orderProduct -> {
                    Product product = orderProduct.getProduct();
                    totalCookingTime.updateAndGet(time -> time.plusMinutes((long) product.getCookingTime().getMinute() * orderProduct.getQuantity()));

                    ProductResponseDTO productResponseDTO = productMapperImpl.toResponseDTO(product);
                    productResponseDTO.setQuantity(orderProduct.getQuantity());
                    return productResponseDTO;
                }).toList();

        orderResponseDTO.setTotalCookingTime(totalCookingTime.get());
        orderResponseDTO.setProducts(productResponseDTOS);

        if (order.isOrderInRestaurant()) {
            response.setOrderInRestaurant(true);
            Table table = order.getTable();
            TableResponseDTO responseDTO = tableMapperImpl.toResponseDTO(table);
            response.setTableResponseDTO(responseDTO);

        } else if (order.isOrderOutRestaurant()) {
            Address address = order.getAddress();
            AddressResponseDTO responseDto = addressMapperImpl.toResponseDto(address);
            response.setAddressResponseDTO(responseDto);
        }
        if (orderDiscountService.existsByOrderId(order.getId())) {
            OrderDiscount orderDiscount = orderDiscountService.findByOrderId(order.getId());
            response.setExistDiscountCodes(true);
            response.setGlobalDiscountCode(orderDiscount.getDiscount().getCode());
        }

        response.setOrderResponseDTO(orderResponseDTO);
        return response;
    }
}
