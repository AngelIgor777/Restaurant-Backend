package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import org.test.restaurant_service.dto.response.JwtResponse;
import org.test.restaurant_service.entity.Otp;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.repository.OtpRepository;
import org.test.restaurant_service.service.OtpService;

import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserServiceImpl userServiceImpl;
    private final JwtServiceImpl jwtServiceImpl;
    private final SecureRandom random = new SecureRandom();


    @Override
    public Otp generateAndSaveOtp(Long chatId) {
        Otp otp = otpRepository.findOtpByChatId(chatId)
                .orElseThrow(EntityNotFoundException::new);


        int otpCode = 100000 + random.nextInt(900000);
        otp.setOtpCode(String.valueOf(otpCode));
        return otpRepository.save(otp);

    }

    public void save(Long chatId, User user) {
        String username = user.getUserName() != null ? user.getUserName() : "unknown";
        String firstname = user.getFirstName();

        Otp otp = Otp.builder()
                .chatId(chatId)
                .username(username)
                .firstname(firstname)
                .verified(false)
                .build();
        save(otp);
    }

    @Override
    public JwtResponse verifyOtpCode(String otpCode) {
        Otp otp = otpRepository.findByOtpCode(otpCode)
                .orElseThrow(() -> new EntityNotFoundException("Otp not found"));
        if (otp.getVerified()) {
            throw new IllegalArgumentException("Otp is already verified");
        }

        otp.setVerified(true);
        save(otp);

        org.test.restaurant_service.entity.User user = org.test.restaurant_service.entity.User.
                builder()
                .otp(otp).build();
        userServiceImpl.save(user);


        String accessToken = jwtServiceImpl.generateUserAccessToken(otp.getChatId(), getUserRoles());

        JwtResponse response = JwtResponse.builder()
                .accessToken(accessToken)
                .userId(user.getId()).build();
        return response;
    }

    @Override
    public Otp save(Otp otp) {
        return otpRepository.save(otp);
    }

    @Override
    public boolean existByChatId(Long chatId) {

        return otpRepository.existsOtpByChatId(chatId);
    }

    @Override
    public List<Otp> getAll() {
        return otpRepository.findAll();
    }

    private List<String> getUserRoles() {
        List<String> roles = new ArrayList<>();

        String roleUser = RoleName.ROLE_USER.toString();
        roles.add(roleUser);
        return roles;
    }
}
