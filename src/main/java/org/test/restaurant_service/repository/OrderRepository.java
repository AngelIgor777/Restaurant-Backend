package org.test.restaurant_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByPaymentMethod(Order.PaymentMethod paymentMethod);

    Optional<Order> findByOtp(String otp);

    List<Order> findAllByStatus(Order.OrderStatus status);

    Optional<Order> findOrderByTable_Number(Integer tableNumber);

    List<Order> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    List<Order> findAllByCreatedAtBetweenAndStatus(LocalDateTime from, LocalDateTime to, Order.OrderStatus status);

    List<Order> findByUser_UuidOrderByCreatedAtDesc(UUID userUUID, Pageable pageable);

    Integer countAllByUser_Uuid(UUID userUUID);

    Integer countAllByUser_TelegramUserEntity_ChatId(Long chatIdp);

    void deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus status, LocalDateTime from, LocalDateTime to);

    boolean existsByOtp(String otp);
}
