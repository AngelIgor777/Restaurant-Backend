package org.test.restaurant_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.restaurant_service.dto.response.JwtResponse;

@RestController
@RequestMapping("/api/v1/login")
public class AuthenticationController {

    //todo
    @PostMapping
    public JwtResponse adminLogin(String username, String password) {
        return null;
    }
}
