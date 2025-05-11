package org.test.restaurant_service.security.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.test.restaurant_service.service.JwtService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest req,
                                   ServerHttpResponse resp,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attrs) {
        String path = req.getURI().getPath();
        // ← skip JWT check for public SockJS endpoint and all its transports
        if (path.startsWith("/ws-open-tables")) {
            return true;
        }

        if (!(req instanceof ServletServerHttpRequest sr)) return false;

        Cookie[] cookies = sr.getServletRequest().getCookies();
        if (cookies == null) return false;

        Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                .filter(c -> "ACCESS_TOKEN".equals(c.getName()))
                .findFirst();

        if (jwtCookie.isEmpty()) return false;

        try {
            String token = jwtCookie.get().getValue();

            List<String> roles = jwtService.getRoles(token);

            if (!roles.contains("ROLE_ADMIN")) return false;      // ⛔ не админ

            // положим Authentication в контекст, чтобы сообщение тоже было авторизовано
            Long chatId = jwtService.getChatId(token);
            List<SimpleGrantedAuthority> auths = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            Authentication auth =
                    new UsernamePasswordAuthenticationToken(chatId, null, auths);
            SecurityContextHolder.getContext().setAuthentication(auth);
            attrs.put("SPRING.AUTHENTICATION", auth);            // для SockJS

            return true;                                         // 👍 handshake OK
        } catch (Exception e) {                                  // подпись/TTL
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
