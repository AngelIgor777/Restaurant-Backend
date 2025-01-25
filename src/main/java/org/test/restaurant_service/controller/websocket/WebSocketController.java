package org.test.restaurant_service.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrdersFromWebsocket(OrderProductResponseWithPayloadDto orderProducts) {
        messagingTemplate.convertAndSend("/topic/orders", orderProducts);
    }
}
