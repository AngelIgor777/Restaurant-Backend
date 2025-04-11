package org.test.restaurant_service.repository;

import org.test.restaurant_service.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByLogin(String login);
    Optional<Admin> findAdminByUser_Uuid(UUID userUUID);

    boolean existsByUserUuid(UUID userUuid);
}