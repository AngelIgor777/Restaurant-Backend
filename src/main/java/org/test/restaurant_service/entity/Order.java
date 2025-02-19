package org.test.restaurant_service.entity;

import javax.persistence.Table;
import javax.persistence.*;

import lombok.*;
import org.test.restaurant_service.service.OrderDiscountService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = "table")
@Entity
@Table(name = "orders", schema = "restaurant_service")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private org.test.restaurant_service.entity.Table table;

    @ManyToOne
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    private String phoneNumber;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(status, order.status);
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isEmpty();
    }

    public boolean hasUser() {
        return user != null;
    }

    public boolean isOrderInRestaurant() {
        return table != null;
    }

    public boolean isOrderOutRestaurant() {
        return address != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }

    public enum PaymentMethod {
        CASH, CARD
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, COMPLETED
    }
}