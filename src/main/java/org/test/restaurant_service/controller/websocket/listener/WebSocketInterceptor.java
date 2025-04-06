package org.test.restaurant_service.controller.websocket.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketInterceptor implements ChannelInterceptor {


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("Client connected: sessionId={}", accessor.getSessionId());
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("Client disconnected: sessionId={}", accessor.getSessionId());
        }
        return message;
    }
}
