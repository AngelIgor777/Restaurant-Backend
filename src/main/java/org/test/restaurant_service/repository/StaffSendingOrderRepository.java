package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.StaffSendingOrder;
import org.test.restaurant_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface StaffSendingOrderRepository extends JpaRepository<StaffSendingOrder, Integer> {
    Optional<StaffSendingOrder> findByChatId(Long chatId);

    List<StaffSendingOrder> findAllBySendingOn(boolean sendingOn);

    void deleteByChatIdAndUser(Long chatId, User user);
}