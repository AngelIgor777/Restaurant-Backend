package org.test.restaurant_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.request.order.ProductsForPrintRequest;
import org.test.restaurant_service.dto.response.printer.OrderForPrintDto;
import org.test.restaurant_service.dto.response.printer.ProductItem;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.entity.OrderProduct;
import org.test.restaurant_service.entity.Product;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PrinterService {
    private static final Logger log = LoggerFactory.getLogger(PrinterService.class);
    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final WebSocketSender webSocketSender;
    private final ObjectMapper jacksonObjectMapper;

    public void sendOrderToPrinter(Integer orderId, @Nullable ProductsForPrintRequest productsId) {
        Order order = orderService.getOrderById(orderId);
        OrderForPrintDto orderForPrintDto = new OrderForPrintDto();
        orderForPrintDto.setCreatedAt(order.getCreatedAt().toString());
        if (order.isOrderInRestaurant()) {
            orderForPrintDto.setTable(order.getTable().getNumber());
        }
        List<ProductItem> productItems;
        if (productsId != null) {
            productItems = getProductItems(orderId, productsId);
        } else {
            productItems = getProductItems(orderId);
        }
        orderForPrintDto.setProductItemList(productItems);
        try {
            String orderForPrintDtoStringInJsonFormat = jacksonObjectMapper.writeValueAsString(orderForPrintDto);
            webSocketSender.sendOrderForLocalPrinter(orderForPrintDto);
            log.debug("Send order for printing: {}", orderForPrintDtoStringInJsonFormat);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert order to json", e);
        }
        orderService.completeOrder(orderId);
    }

    private List<ProductItem> getProductItems(Integer orderId, ProductsForPrintRequest productsId) {
        List<ProductItem> productItems = orderProductService.getOrderProductsByOrderId(orderId)
                .stream()
                .filter(orderProduct -> productsId.getProducts().contains(orderProduct.getProduct().getId()))
                .map(getOrderProductProductItem()).toList();
        return productItems;
    }

    private List<ProductItem> getProductItems(Integer orderId) {
        List<ProductItem> productItems = orderProductService.getOrderProductsByOrderId(orderId)
                .stream()
                .map(getOrderProductProductItem()).toList();
        return productItems;
    }

    private Function<OrderProduct, ProductItem> getOrderProductProductItem() {
        return orderProduct -> {
            Product product = orderProduct.getProduct();
            String productTypeName = product.getType().getName();
            String productName = product.getName();
            Integer quantity = orderProduct.getQuantity();
            BigDecimal price = product.getPrice().multiply(new BigDecimal(quantity));
            return new ProductItem(productTypeName, productName, quantity, price);
        };
    }

}
