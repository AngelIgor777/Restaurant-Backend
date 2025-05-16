package org.test.restaurant_service.service;

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
import org.test.restaurant_service.entity.Table;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// ESC/POS-команды

@Service
public class PrinterService {
    private static final Logger log = LoggerFactory.getLogger(PrinterService.class);

    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final WebSocketSender webSocketSender;

    private static final String ENCODING = "CP866";
    private static final String RESTAURANT_NAME = "PARK TOWN";

    private static final byte[] SET_CODE_PAGE_CYRILLIC = new byte[]{0x1B, 't', 7};
    private static final byte[] INIT_PRINTER = new byte[]{0x1B, '@'};
    private static final byte[] ALIGN_CENTER = new byte[]{0x1B, 'a', 1};
    private static final byte[] ALIGN_LEFT = new byte[]{0x1B, 'a', 0};
    private static final byte[] FONT_DOUBLE_HEIGHT = new byte[]{0x1D, '!', 0x11};
    private static final byte[] FONT_NORMAL = new byte[]{0x1D, '!', 0x00};
    private static final byte[] CUT_COMMAND = new byte[]{0x1D, 'V', 1};

    public PrinterService(OrderService orderService,
                          OrderProductService orderProductService,
                          WebSocketSender webSocketSender) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.webSocketSender = webSocketSender;
    }

    public void sendOrderToPrinter(Integer orderId,
                                   @Nullable ProductsForPrintRequest productsId,
                                   @Nullable Order inputOrder) {
        Order order = inputOrder != null
                ? inputOrder
                : orderService.getOrderById(orderId);

        if (order == null) {
            log.warn("Order not found for ID: {}", orderId);
            return;
        }

        // Собираем DTO, чтобы получить поля
        OrderForPrintDto dto = toDto(orderId, productsId, order);

        // Генерим ESC/POS-байты
        byte[] rawBytes = buildEscPosBytes(dto);

        // Отправляем их по WebSocket в бинарном фрейме
        webSocketSender.sendRawToPrinter(rawBytes);

        // И, как раньше, меняем статус заказа
        if (!order.getStatus().equals(Order.OrderStatus.COMPLETED)) {
            Table table = order.getTable();
            orderService.completeOrder(orderId, table != null ? table.getId() : null);
        }
    }

    private OrderForPrintDto toDto(Integer orderId,
                                   @Nullable ProductsForPrintRequest productsId,
                                   Order order) {
        var dto = new OrderForPrintDto();
        dto.setCreatedAt(order.getCreatedAt().toString());
        if (order.orderInRestaurant() && order.getTable() != null) {
            dto.setTable(order.getTable().getNumber());
        }
        List<ProductItem> items = orderProductService.getOrderProductsByOrderId(orderId)
                .stream()
                .filter(op -> productsId == null || productsId.getProducts().contains(op.getProduct().getId()))
                .map(toProductItem())
                .toList();
        dto.setProductItemList(items);
        return dto;
    }

    private Function<OrderProduct, ProductItem> toProductItem() {
        return op -> {
            Product p = op.getProduct();
            int qty = op.getQuantity();
            BigDecimal total = p.getPrice().multiply(BigDecimal.valueOf(qty));
            return new ProductItem(p.getType().getName(), p.getName(), qty, total);
        };
    }

    private byte[] buildEscPosBytes(OrderForPrintDto order) {
        try (var out = new ByteArrayOutputStream()) {
            // Header
            out.write(INIT_PRINTER);
            out.write(SET_CODE_PAGE_CYRILLIC);
            out.write(ALIGN_CENTER);
            out.write(FONT_DOUBLE_HEIGHT);
            out.write((RESTAURANT_NAME + "\n").getBytes(ENCODING));
            out.write(FONT_NORMAL);
            out.write("-----------------------------\n".getBytes(ENCODING));

            // Details
            out.write(ALIGN_LEFT);
            out.write(FONT_DOUBLE_HEIGHT);
            if (order.getTable() != null) {
                out.write(("Стол №: " + order.getTable() + "\n").getBytes(ENCODING));
            }
            String time = LocalDateTime
                    .parse(order.getCreatedAt())
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
            out.write(("Время: " + time + "\n").getBytes(ENCODING));
            out.write("-----------------------------\n".getBytes(ENCODING));

            // Items
            for (var item : order.getProductItemList()) {
                String name = Arrays.stream(item.getName().split("\\s+"))
                        .map(w -> {
                            String up = w.toUpperCase();
                            return up.length() > 4 ? up.substring(0, 4) : up;
                        })
                        .collect(Collectors.joining(". "));
                String first = cutText(name, 20);
                String second = name.length() > 20 ? name.substring(20) : "";

                out.write(FONT_DOUBLE_HEIGHT);
                out.write(String.format("%dx %-20s\n", item.getQuantity(), first)
                        .getBytes(ENCODING));
                if (!second.isEmpty()) {
                    out.write(String.format("   %-20s\n", second)
                            .getBytes(ENCODING));
                }
                out.write(FONT_NORMAL);
            }

            // Cut
//            out.write(CUT_COMMAND);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка генерации ESC/POS-байтов: {}", e.getMessage(), e);
            return new byte[0];
        }
    }
    private String cutText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength);
        }
        return text;
    }
}
