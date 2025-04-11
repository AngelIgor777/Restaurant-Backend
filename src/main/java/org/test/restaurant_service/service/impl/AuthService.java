package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.admin.AdminDataRequest;
import org.test.restaurant_service.dto.request.admin.PasswordChangeRequest;
import org.test.restaurant_service.dto.response.admin.JwtResponse;
import org.test.restaurant_service.entity.Admin;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.AdminRepository;
import org.test.restaurant_service.service.JwtService;
import org.test.restaurant_service.service.UserService;
import org.test.restaurant_service.util.UserPasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final UserPasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public JwtResponse login(AdminDataRequest request) {
        Admin admin = adminRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        User user = admin.getUser();

        return jwtService.generateJwtResponseForAdmin(admin, user);
    }

    @Transactional(readOnly = true)
    public boolean isRegistered(UUID userUUID) {
        return adminRepository.existsByUserUuid(userUUID);
    }

    @Transactional(rollbackFor = Exception.class)
    public JwtResponse register(AdminDataRequest request, UUID userUUID) {
        User user = userService.findByUUID(userUUID);
        if (!user.isAdmin()) {
            throw new IllegalArgumentException("User is not admin");
        }
        Admin admin = new Admin();
        admin.setUser(user);
        admin.setLogin(request.getLogin());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setUpdatedAt(LocalDateTime.now());
        Admin savedAdmin = adminRepository.save(admin);
        return jwtService.generateJwtResponseForAdmin(savedAdmin, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UUID userUUID, PasswordChangeRequest request) {
        Admin admin = adminRepository.findAdminByUser_Uuid(userUUID)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        boolean equalsUUID = admin.getUser().getUuid().equals(userUUID);
        boolean equalsLogin = admin.getLogin().equals(request.getLogin());
        if (!equalsLogin || !equalsUUID || !passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }
        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminRepository.save(admin);
    }
}
