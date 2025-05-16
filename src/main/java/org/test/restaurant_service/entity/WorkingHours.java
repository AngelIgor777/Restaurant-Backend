package org.test.restaurant_service.entity;

import lombok.*;

import javax.persistence.*;
import javax.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "working_hours", schema = "restaurant_service")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class WorkingHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private short dayOfWeek;

    private LocalTime openTime;

    private LocalTime closeTime;
}