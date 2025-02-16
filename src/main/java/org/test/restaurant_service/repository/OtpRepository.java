package org.test.restaurant_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.OtpCode;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Integer> {

    @Query("SELECT o FROM OtpCode o " +
            "JOIN User u ON o.user.uuid = u.uuid " +
            "JOIN TelegramUserEntity t ON u.telegramUserEntity.chatId = :chatId " +
            "WHERE o.otpCode = :otpCode " +
            "AND o.expiresAt > CURRENT_TIMESTAMP " +
            "AND o.isUsed = false ")
    Optional<OtpCode> findValidOtp(Long chatId, String otpCode);

    @Query("SELECT o FROM OtpCode  o " +
            "JOIN User u ON u.telegramUserEntity.chatId = :chatId " +
            "WHERE o.expiresAt > CURRENT_TIMESTAMP " +
            "AND o.isUsed = FALSE ")
    Optional<OtpCode> findExistCode(Long chatId);
}