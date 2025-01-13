package org.test.restaurant_service.entity;

import javax.persistence.Table;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Builder
@Getter
@Setter
@ToString(exclude = "product")
@RequiredArgsConstructor
@Entity
@Table(name = "photo", schema = "restaurant_service")
@AllArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_photo_product"))
    private Product product;

    @Column(nullable = false, length = 256)
    private String url;

    @Transient
    @JsonIgnore
    private MultipartFile image;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(id, photo.id) && Objects.equals(url, photo.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url);
    }
}
