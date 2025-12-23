package org.josiasguerrero.products.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductJpaEntity {

  @Id
  @Column(columnDefinition = "BINARY(16)")
  private byte[] id;

  @Column(unique = true, nullable = false, length = 50)
  private String sku;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 255)
  private String description;

  @Column(unique = true, length = 50)
  private String barcode;

  @Column(nullable = false)
  private Integer stock;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cost;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "brand_id")
  private Integer brandId;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<CategoryJpaEntity> categories;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<ProductPropertyJpaEntity> properties = new HashSet<>();

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PrePersist
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public void addProperty(ProductPropertyJpaEntity property) {
    properties.add(property);
    property.setProduct(this);
  }

  public void removeProperty(ProductPropertyJpaEntity property) {
    properties.remove(property);
    property.setProduct(null);
  }

  public void clearProperties() {
    properties.forEach(prop -> prop.setProduct(null));
    properties.clear();
  }
}
