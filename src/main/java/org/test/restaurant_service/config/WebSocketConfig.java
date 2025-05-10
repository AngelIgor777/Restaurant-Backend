package org.test.restaurant_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.test.restaurant_service.security.ws.JwtHandshakeInterceptor;
import org.test.restaurant_service.security.ws.StompAuthChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-orders-print")
                .addInterceptors(jwtHandshakeInterceptor);

        registry.addEndpoint("/ws-orders")
                .setAllowedOriginPatterns("http://localhost:63344")
                .setAllowedOriginPatterns("http://localhost:9092")
                .setAllowedOriginPatterns("http://127.0.0.1:9092")
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS();

        registry.addEndpoint("/ws-open-tables")
                .setAllowedOriginPatterns("http://localhost:63344")
                .setAllowedOriginPatterns("http://localhost:9092")
                .setAllowedOriginPatterns("http://127.0.0.1:9092")
                .setAllowedOriginPatterns()
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
