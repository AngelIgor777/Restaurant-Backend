package org.test.restaurant_service.repository;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findOrderByTable_Number(Integer tableNumber);

    List<Order> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<Order> findAllByCreatedAtBetweenAndStatus(LocalDateTime from, LocalDateTime to, Order.OrderStatus status);
}
