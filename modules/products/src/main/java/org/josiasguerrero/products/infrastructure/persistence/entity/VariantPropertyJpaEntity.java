package org.josiasguerrero.products.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "variant_property", uniqueConstraints = @UniqueConstraint(columnNames = { "variant_id", "property_id" }))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = { "variant", "property" })
public class VariantPropertyJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "variant_id")
  private ProductVariantJpaEntity variant;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "property_id")
  private PropertyJpaEntity property;

  private String value;
}
