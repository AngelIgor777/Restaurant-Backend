package org.test.restaurant_service.entity;

import lombok.*;

import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_user", schema = "restaurant_service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramUserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    @Column
    private String username;

    private String photoUrl;

    @Column(nullable = false)
    private String firstname;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    public boolean langIsSelected() {
        return language != null;
    }
}
