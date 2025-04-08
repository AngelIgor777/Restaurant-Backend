package org.test.restaurant_service.dto.request.admin;


import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}