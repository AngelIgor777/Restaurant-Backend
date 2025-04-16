package org.test.restaurant_service.entity;


import lombok.*;

import javax.persistence.*;
import javax.persistence.Table;

@Table(name = "staff_sending_order", schema = "restaurant_service",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_uuid", "sending_on"}))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class StaffSendingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long chatId;

    @OneToOne
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
    private User user;

    @Column(name = "sending_on")
    private boolean sendingOn = false;
}
