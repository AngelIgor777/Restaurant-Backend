package org.test.restaurant_service.entity;

import lombok.*;

import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_tg_codes", schema = "restaurant_service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long chatId;

    private String otpCode;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @OneToOne(mappedBy = "otp",cascade = CascadeType.ALL)
    private User user;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private final LocalDateTime createdAt = LocalDateTime.now();
}
