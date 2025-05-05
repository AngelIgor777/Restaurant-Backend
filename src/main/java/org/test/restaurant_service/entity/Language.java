package org.test.restaurant_service.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Entity
@Table(name = "language", schema = "restaurant_service")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @EqualsAndHashCode.Include
    private String code;

    private String name;

    @OneToMany(mappedBy = "language",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TelegramUserEntity> telegramUserEntity;
}