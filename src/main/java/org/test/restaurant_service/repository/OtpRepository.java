package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
}
