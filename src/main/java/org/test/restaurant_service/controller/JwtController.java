package org.test.restaurant_service.controller;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.service.JwtService;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jwt")
@RequiredArgsConstructor
public class JwtController {

    private static final Logger log = LoggerFactory.getLogger(JwtController.class);
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> generateJwtForUser(@RequestParam UUID userUUID) {
        log.debug("Received request to generate JWT for user {}", userUUID);

        String jwt = jwtService.generateUserAccessToken(userUUID).getAccessToken();

        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", jwt)
                .httpOnly(true)          // защищает от XSS
                .secure(false)            // отправлять только по HTTPS
                .sameSite("None")      // защита от CSRF
                .path("/")               // ко всем эндпоинтам
                .maxAge(Duration.ofMinutes(30)) // TTL = 30 минут
                .build();

        log.debug("JWT generated and written to cookie for user {}", userUUID);

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString()).build();
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        var cookie = ResponseCookie.from("ACCESS_TOKEN", "")
                .maxAge(0)              // удаляем
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/check-admin")
    public ResponseEntity<Void> checkAdmin(Authentication auth) {
        if (auth != null && auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok().build();      // 200 → nginx даст файл
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 → nginx редирект
    }
}
