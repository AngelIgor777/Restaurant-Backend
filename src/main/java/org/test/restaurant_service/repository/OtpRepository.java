package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.Otp;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Integer> {

    Optional<Otp> findByOtpCode(String otpCode);

}
