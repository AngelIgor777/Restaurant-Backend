package org.test.restaurant_service.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.test.restaurant_service.dto.request.table.OpenTables;
import org.test.restaurant_service.dto.response.OrdersStatesCount;
import org.test.restaurant_service.dto.response.WaiterCallRequestDTO;
import org.test.restaurant_service.dto.response.printer.OrderForPrintDto;

@Controller
@RequiredArgsConstructor
public class WebSocketSender {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSender.class);
    private final SimpMessagingTemplate messagingTemplate;

    public void sendOpenTables(OpenTables openTables) {
        messagingTemplate.convertAndSend("/topic/open-tables", openTables);
    }

    public void sendOrdersStateCount(OrdersStatesCount tableOrderInfo) {
        messagingTemplate.convertAndSend("/topic/tables-info", tableOrderInfo);
    }

    public void sendCallToWaiter(WaiterCallRequestDTO callWaiterDTO) {
        messagingTemplate.convertAndSend("/topic/call-waiter", callWaiterDTO);
    }

    public void sendCode(int code) {
        messagingTemplate.convertAndSend("/topic/code", code);
    }

    public void sendRawToPrinter(byte[] data) {
        Message<byte[]> message = MessageBuilder
                .withPayload(data)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_OCTET_STREAM)
                .build();

        messagingTemplate.send("/topic/orders-print", message);

    }


}
