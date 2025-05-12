package org.test.restaurant_service.security.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand cmd = accessor.getCommand();

        // 1) CONNECT — пропускаем, даже без auth
        if (StompCommand.CONNECT.equals(cmd)) {
            return message;
        }

        // 2) SUBSCRIBE — вешаем авторизацию
        if (StompCommand.SUBSCRIBE.equals(cmd)) {
            String dest = accessor.getDestination();
            // публичный топик — всегда ОК
            if ("/topic/open-tables".equals(dest)) {
                return message;
            }

            // всё остальное — только для авторизованных с ролью ADMIN или MODERATOR
            Authentication auth = (Authentication) accessor.getUser();
            if (auth == null ||
                    auth.getAuthorities().stream().noneMatch(a ->
                            a.getAuthority().equals("ROLE_ADMIN") ||
                                    a.getAuthority().equals("ROLE_MODERATOR")
                    )) {
                throw new AccessDeniedException("Only admins can subscribe to " + dest);
            }
        }

        return message;
    }
}
