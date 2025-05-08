package org.test.restaurant_service.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.test.restaurant_service.dto.request.table.OpenTables;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;
import org.test.restaurant_service.dto.response.OrdersStatesCount;
import org.test.restaurant_service.dto.response.WaiterCallRequestDTO;
import org.test.restaurant_service.dto.response.printer.OrderForPrintDto;

@Controller
@RequiredArgsConstructor
public class WebSocketSender {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSender.class);
    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrdersFromWebsocket(OrderProductResponseWithPayloadDto orderProducts) {
        messagingTemplate.convertAndSend("/topic/orders", orderProducts);
    }

    public void sendOrderForLocalPrinter(OrderForPrintDto orderForPrintDto) {
        log.debug("Sending order for print: {}", orderForPrintDto);
        messagingTemplate.convertAndSend("/topic/orders-print", orderForPrintDto);
    }

    public void sendOpenTables(OpenTables openTables) {
        messagingTemplate.convertAndSend("/topic/open-tables", openTables);
    }

    public void sendTablesOrderInfo(OrdersStatesCount tableOrderInfo) {
        messagingTemplate.convertAndSend("/topic/tables-info", tableOrderInfo);
    }

    public void sendCallToWaiter(WaiterCallRequestDTO callWaiterDTO) {
        messagingTemplate.convertAndSend("/topic/call-waiter", callWaiterDTO);
    }

    public void sendCode(int code) {
        messagingTemplate.convertAndSend("/topic/code", code);
    }

}
