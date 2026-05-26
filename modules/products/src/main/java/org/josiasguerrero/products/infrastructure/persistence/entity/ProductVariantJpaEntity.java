package org.josiasguerrero.products.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantJpaEntity {

  @Id
  private UUID id;

  @Column(unique = true)
  private String sku;

  @Column(unique = true)
  private String barcode;

  private Integer stock;

  private BigDecimal cost;

  private BigDecimal price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private ProductJpaEntity product;

  @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<VariantPropertyJpaEntity> properties = new java.util.HashSet<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public void addProperty(VariantPropertyJpaEntity property) {
    properties.add(property);
    property.setVariant(this);
  }

  public void removeProperty(VariantPropertyJpaEntity property) {
    properties.remove(property);
    property.setVariant(null);
  }

  public void clearProperties() {
    properties.forEach(prop -> prop.setVariant(null));
    properties.clear();
  }
}
