package org.test.restaurant_service.entity;


import lombok.*;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name = "ui_translations", schema = "restaurant_service", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"key", "lang_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UiTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "key", nullable = false)
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lang_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ui_translation_language"))
    private Language language;

    @Column(name = "value", nullable = false)
    private String value;
}