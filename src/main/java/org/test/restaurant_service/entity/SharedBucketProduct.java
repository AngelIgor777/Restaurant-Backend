package org.test.restaurant_service.entity;

import lombok.*;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name = "shared_bucket_products", schema = "restaurant_service",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shared_bucket_id", "product_id", "user_uuid"}))
@Getter
@Setter
@ToString(exclude = {"user", "sharedBucket", "product"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedBucketProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_bucket_id", nullable = false)
    private SharedBucket sharedBucket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid")
    private User user;

    @Column(nullable = false)
    @Builder.Default
    @EqualsAndHashCode.Include
    private Integer quantity = 1;
}
