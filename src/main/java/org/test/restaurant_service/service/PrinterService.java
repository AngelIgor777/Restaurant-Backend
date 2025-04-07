package org.test.restaurant_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketController;
import org.test.restaurant_service.dto.response.printer.OrderForPrintDto;
import org.test.restaurant_service.dto.response.printer.ProductItem;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrinterService {
    private static final Logger log = LoggerFactory.getLogger(PrinterService.class);
    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final WebSocketController webSocketController;
    private final ObjectMapper jacksonObjectMapper;

    public void sendOrderToPrinter(Integer orderId) {
        Order order = orderService.getOrderById(orderId);
        OrderForPrintDto orderForPrintDto = new OrderForPrintDto();
        orderForPrintDto.setCreatedAt(order.getCreatedAt().toString());
        orderForPrintDto.setTotalPrice(order.getTotalPrice());
        orderForPrintDto.setPaymentMethod(order.getPaymentMethod().name());
        if (order.isOrderInRestaurant()) {
            orderForPrintDto.setTable(order.getTable().getNumber());
        } else if (order.isOrderOutRestaurant()) {
            if (order.getPhoneNumber() != null) {
                orderForPrintDto.setPhoneNumber(order.getPhoneNumber());
            }
            Address address = order.getAddress();
            if (address != null) {
                org.test.restaurant_service.dto.response.printer.Address
                        addressForPrinter = new org.test.restaurant_service.dto.response.printer.Address(
                        address.getCity(),
                        address.getStreet(),
                        address.getHomeNumber(),
                        address.getApartmentNumber()
                );
                orderForPrintDto.setAddress(addressForPrinter);
            }
        }

        List<ProductItem> productItems = orderProductService.getOrderProductsByOrderId(orderId)
                .stream()
                .map(orderProduct -> {
                    Product product = orderProduct.getProduct();
                    String productTypeName = product.getType().getName();
                    String productName = product.getName();
                    Integer quantity = orderProduct.getQuantity();
                    BigDecimal price = product.getPrice().multiply(new BigDecimal(quantity));
                    return new ProductItem(productTypeName, productName, quantity, price);
                }).toList();
        orderForPrintDto.setProductItemList(productItems);
        try {
            String orderForPrintDtoStringInJsonFormat = jacksonObjectMapper.writeValueAsString(orderForPrintDto);
            webSocketController.sendOrderForLocalPrinter(orderForPrintDto);
            log.debug("Send order for printing: {}", orderForPrintDtoStringInJsonFormat);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert order to json", e);
        }
        orderService.completeOrder(orderId);
    }

}
