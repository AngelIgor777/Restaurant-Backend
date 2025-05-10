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

        if (accessor == null) return message;

        // 1) CONNECT — проверяем JWT и ставим Authentication
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // User уже положен в accessor в JwtHandshakeInterceptor
            Authentication auth = (Authentication) accessor.getUser();
            if (auth == null) {
                throw new MessagingException("JWT not found in handshake");
            }
        }


        // 2) SUBSCRIBE — проверяем, что currentUser — админ
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String dest = accessor.getDestination();
            Authentication auth = (Authentication) accessor.getUser();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                            a.getAuthority().equals("ROLE_MODERATOR"));

            // Все могут open-tables и orders-print (если нужно)
            if ("/topic/open-tables".equals(dest)) {
                return message;
            }

            if (dest.startsWith("/topic/") && !isAdmin) {
                throw new AccessDeniedException("Only admins can subscribe to " + dest);
            }
        }

        return message;
    }
}
