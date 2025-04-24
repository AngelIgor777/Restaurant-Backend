package org.test.restaurant_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-orders-print")
                .setAllowedOrigins("http://localhost:8080")
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*", "http://195.133.27.38:*");

        registry.addEndpoint("/ws-orders")
                .setAllowedOrigins("http://195.133.27.38")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
