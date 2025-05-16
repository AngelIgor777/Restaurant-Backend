package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadAndPrintRequestDto;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadRequestDto;
import org.test.restaurant_service.dto.response.OrderProductResponseDTO;
import org.test.restaurant_service.dto.response.OtpResponseDto;
import org.test.restaurant_service.entity.OrderProduct;
import org.test.restaurant_service.mapper.OrderProductMapper;
import org.test.restaurant_service.rabbitmq.producer.RabbitMQJsonProducer;
import org.test.restaurant_service.service.OrderProductService;
import org.test.restaurant_service.service.impl.WorkingHoursService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order-products")
@RequiredArgsConstructor
public class OrderProductController {

    private final OrderProductService orderProductService;
    private final OrderProductMapper orderProductMapper;
    private final RabbitMQJsonProducer producer;
    private final WorkingHoursService workingHoursService;

    @GetMapping("/order/{orderId}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public List<OrderProductResponseDTO> getOrderProductsByOrderId(@PathVariable Integer orderId) {
        List<OrderProduct> orderProductsByOrderId = orderProductService.getOrderProductsByOrderId(orderId);
        return orderProductsByOrderId.stream().map(orderProductMapper::toResponseDTO).toList();
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("!#dto.orderInRestaurant or @securityService.userIsActivated(authentication)")
    public OtpResponseDto createBulk(@Valid @RequestBody OrderProductWithPayloadRequestDto dto) {
        if (!workingHoursService.isNowInWorkingTime()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Заказы принимаются только в рабочее время");
        }

        return producer.send(dto);
    }


    @PostMapping("/bulk/admin")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    @ResponseStatus(HttpStatus.CREATED)
    public OtpResponseDto createBulkAdmin(@Valid @RequestBody OrderProductWithPayloadAndPrintRequestDto requestDtoWithPayloadDto) {
        return producer.send(requestDtoWithPayloadDto);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        orderProductService.delete(id);
    }
}
