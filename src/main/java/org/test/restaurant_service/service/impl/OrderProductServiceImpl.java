package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketController;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseDTO;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.OrderProduct;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.Table;
import org.test.restaurant_service.mapper.OrderMapper;
import org.test.restaurant_service.mapper.OrderProductMapper;
import org.test.restaurant_service.repository.OrderProductRepository;
import org.test.restaurant_service.repository.OrderRepository;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.repository.TableRepository;
import org.test.restaurant_service.service.OrderProductService;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderProductServiceImpl implements OrderProductService {
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductMapper orderProductMapper;
    private final TableRepository tableRepository;
    private final OrderMapper orderMapper;

    private final WebSocketController webSocketController;

    @Override
    public List<OrderProductResponseDTO> createBulk(List<OrderProductRequestDTO> requestDTOs, Integer tableNumber, Order.PaymentMethod paymentMethod) {
        Table table = tableRepository.findTablesByNumber(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with number " + tableNumber));


        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.valueOf(0));

        Order order = Order.builder()
                .table(table)
                .paymentMethod(paymentMethod)
                .build();


        List<OrderProduct> orderProducts = requestDTOs.stream()
                .map(requestDTO -> {

                    Product product = productRepository.findById(requestDTO.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + requestDTO.getProductId()));
                    OrderProduct orderProduct = orderProductMapper.toEntity(requestDTO);
                    orderProduct.setOrder(order);
                    orderProduct.setProduct(product);

                    totalPrice.updateAndGet(v -> v.add(product.getPrice()));

                    return orderProduct;
                })
                .collect(Collectors.toList());

        order.setTotalPrice(totalPrice.get());
        // Сохраняем все заказанные продукты
        orderRepository.save(order);
        orderProductRepository.saveAll(orderProducts);

        // Возвращаем список DTO для всех заказов
        List<OrderProductResponseDTO> orderProductResponse = orderProducts.stream()
                .map(orderProductMapper::toResponseDTO)
                .collect(Collectors.toList());

        sendOrdersFromWebsocket();
        return orderProductResponse;
    }

    private void sendOrdersFromWebsocket() {
        List<OrderResponseDTO> allOrders = orderRepository.findAll().stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
        webSocketController.sendOrdersFromWebsocket(allOrders);
    }

    @Override
    public List<OrderProductResponseDTO> getOrderProductsByOrderId(Integer orderId) {
        return orderProductRepository.findAllByOrderId(orderId)
                .stream()
                .map(orderProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderProductResponseDTO update(Integer id, OrderProductRequestDTO requestDTO, Integer orderId) {
        OrderProduct orderProduct = orderProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderProduct not found with id " + id));
        orderProductMapper.updateEntityFromRequestDTO(requestDTO, orderProduct);

        if (orderId != null) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + orderId));
            orderProduct.setOrder(order);
        }
        if (requestDTO.getProductId() != null) {
            Product product = productRepository.findById(requestDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + requestDTO.getProductId()));
            orderProduct.setProduct(product);
        }
        orderProduct = orderProductRepository.save(orderProduct);
        return orderProductMapper.toResponseDTO(orderProduct);
    }

    @Override
    public void delete(Integer id) {
        if (!orderProductRepository.existsById(id)) {
            throw new EntityNotFoundException("OrderProduct not found with id " + id);
        }
        orderProductRepository.deleteById(id);
    }
}
