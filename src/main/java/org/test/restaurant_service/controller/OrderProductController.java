package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadAndPrintRequestDto;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadRequestDto;
import org.test.restaurant_service.dto.response.OrderProductResponseDTO;
import org.test.restaurant_service.dto.response.OtpResponseDto;
import org.test.restaurant_service.entity.OrderProduct;
import org.test.restaurant_service.mapper.OrderProductMapper;
import org.test.restaurant_service.rabbitmq.producer.RabbitMQJsonProducer;
import org.test.restaurant_service.service.OrderProductService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order-products")
@RequiredArgsConstructor
public class OrderProductController {

    private final OrderProductService orderProductService;
    private final OrderProductMapper orderProductMapper;
    private final RabbitMQJsonProducer producer;

    @GetMapping("/order/{orderId}")
    public List<OrderProductResponseDTO> getOrderProductsByOrderId(@PathVariable Integer orderId) {
        List<OrderProduct> orderProductsByOrderId = orderProductService.getOrderProductsByOrderId(orderId);
        return orderProductsByOrderId.stream().map(orderProductMapper::toResponseDTO).toList();
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public OtpResponseDto createBulk(@Valid @RequestBody OrderProductWithPayloadRequestDto requestDtoWithPayloadDto) {
        return producer.send(requestDtoWithPayloadDto);
    }

    @PostMapping("/bulk/admin")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    @ResponseStatus(HttpStatus.CREATED)
    public OtpResponseDto createBulkAdmin(@Valid @RequestBody OrderProductWithPayloadAndPrintRequestDto requestDtoWithPayloadDto) {
        return producer.send(requestDtoWithPayloadDto);
    }

    @PatchMapping("/{id}")
    public OrderProductResponseDTO update(@PathVariable Integer id,
                                          @Valid @RequestBody OrderProductRequestDTO requestDTO,
                                          @RequestParam Integer orderId) {
        return orderProductService.update(id, requestDTO, orderId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        orderProductService.delete(id);
    }
}
