package org.test.restaurant_service.entity;

import javax.persistence.Table;
import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@ToString(exclude = "order")
@Entity
@Table(name = "reviews", schema = "restaurant_service")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "visitor_name", nullable = false)
    private String visitorName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}