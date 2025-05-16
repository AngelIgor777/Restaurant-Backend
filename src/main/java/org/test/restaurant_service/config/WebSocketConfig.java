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


    String[] PUBLIC_ORIGINS = {
            "http://localhost:63344",
            "http://127.0.0.1:63344",
            "http://localhost:63342",

            "http://localhost:63343",
            "http://127.0.0.1:63343",

            "http://localhost:9092",
            "http://127.0.0.1:9092",

    };

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-orders-print")
                .setAllowedOrigins(PUBLIC_ORIGINS)

                .addInterceptors(jwtHandshakeInterceptor);

        registry.addEndpoint("/ws-orders")
                .setAllowedOrigins(PUBLIC_ORIGINS)

                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS();

        registry.addEndpoint("/ws-open-tables")
                .setAllowedOrigins(PUBLIC_ORIGINS)

                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
