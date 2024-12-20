package org.test.restaurant_service.entity;

import javax.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@javax.persistence.Table(name = "tables", schema = "restaurant_service")
public class Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer number;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(id, table.id) && Objects.equals(number, table.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number);
    }
}