package org.test.restaurant_service.entity;

import lombok.*;

import javax.persistence.Table;
import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name = "roles", schema = "restaurant_service")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private RoleName roleName;

}