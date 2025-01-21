package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.controller.websocket.WebSocketController;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseDTO;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.OrderProduct;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.Table;
import org.test.restaurant_service.mapper.OrderMapper;
import org.test.restaurant_service.mapper.OrderProductMapper;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.repository.OrderProductRepository;
import org.test.restaurant_service.repository.OrderRepository;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.repository.TableRepository;
import org.test.restaurant_service.service.OrderProductService;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO createBulk(List<OrderProductRequestDTO> requestDTOs, Integer tableNumber, Order.PaymentMethod paymentMethod) {
        Table table = existTable(tableNumber);
        Order order = Order.builder()
                .table(table)
                .paymentMethod(paymentMethod)
                .build();
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();
        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.valueOf(0));
        AtomicReference<LocalTime> totalCookingTime = new AtomicReference<>(LocalTime.of(0, 0, 0, 0));
        List<OrderProduct> orderProducts = getOrderProducts(requestDTOs, order, totalPrice, totalCookingTime, productResponseDTOList);
        order.setTotalPrice(totalPrice.get());

        // Сохраняем все заказанные продукты
        orderRepository.save(order);
        orderProductRepository.saveAll(orderProducts);


        OrderResponseDTO orderResponse = orderMapper.toResponseDTO(order);
        orderResponse.setProducts(productResponseDTOList);
        orderResponse.setTotalCookingTime(totalCookingTime.get());
        sendOrdersFromWebsocket();
        return orderResponse;
    }

    private List<OrderProduct> getOrderProducts(List<OrderProductRequestDTO> requestDTOs, Order order, AtomicReference<BigDecimal> totalPrice, AtomicReference<LocalTime> totalCookingTime, List<ProductResponseDTO> productResponseDTOList) {
        return requestDTOs.stream()
                .map(requestDTO -> {
                    Product product = productRepository.findById(requestDTO.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + requestDTO.getProductId()));
                    OrderProduct orderProduct = createOrderProduct(order, requestDTO, product);
                    countTotalPrice(totalPrice, product);
                    countTotalCookingTime(totalCookingTime, product);
                    ProductResponseDTO productResponseDTO = productMapper.toResponseDTO(product);
                    countQuantity(requestDTO, productResponseDTO);
                    productResponseDTOList.add(productResponseDTO);
                    return orderProduct;
                })
                .collect(Collectors.toList());
    }

    private OrderProduct createOrderProduct(Order order, OrderProductRequestDTO requestDTO, Product product) {
        OrderProduct orderProduct = orderProductMapper.toEntity(requestDTO);
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        return orderProduct;
    }

    private void countQuantity(OrderProductRequestDTO requestDTO, ProductResponseDTO productResponseDTO) {
        productResponseDTO.setQuantity(requestDTO.getQuantity());
    }

    private LocalTime countTotalCookingTime(AtomicReference<LocalTime> totalCookingTime, Product product) {
        return totalCookingTime.updateAndGet(t -> t.plusMinutes(product.getCookingTime().getMinute())
                .plusSeconds(product.getCookingTime().getSecond()));
    }

    private BigDecimal countTotalPrice(AtomicReference<BigDecimal> totalPrice, Product product) {
        return totalPrice.updateAndGet(v -> v.add(product.getPrice()));
    }


    private Table existTable(Integer tableNumber) {
        Table table = tableRepository.findTablesByNumber(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with number " + tableNumber));
        return table;
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
