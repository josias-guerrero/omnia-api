package org.josiasguerrero.products.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
  private UUID id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 255)
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "brand_id")
  private BrandJpaEntity brand;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
  @Builder.Default
  private Set<CategoryJpaEntity> categories = new HashSet<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<ProductVariantJpaEntity> variants = new HashSet<>();

}
