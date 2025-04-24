package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.mapper.OrderProductMapper;
import org.test.restaurant_service.repository.*;
import org.test.restaurant_service.service.OrderProductService;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderProductServiceImpl implements OrderProductService {
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductMapper orderProductMapper;
    private final WebSocketSender webSocketSender;


    public void createAll(List<OrderProduct> orderProducts) {
        orderProductRepository.saveAll(orderProducts);
    }

    public void sendOrdersFromWebsocket(OrderProductResponseWithPayloadDto payloadDto) {
        webSocketSender.sendOrdersFromWebsocket(payloadDto);
    }

    @Override
    public List<OrderProduct> getOrderProductsByOrderId(Integer orderId) {
        return orderProductRepository.findAllByOrderId(orderId);
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
