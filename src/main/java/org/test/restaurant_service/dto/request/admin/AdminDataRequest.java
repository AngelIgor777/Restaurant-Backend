package org.test.restaurant_service.dto.request.admin;

import lombok.Data;

@Data
public class AdminDataRequest {
    private String login;
    private String password;
}