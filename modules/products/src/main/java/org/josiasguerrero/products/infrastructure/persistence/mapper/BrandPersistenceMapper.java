package org.josiasguerrero.products.infrastructure.persistence.mapper;

import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.infrastructure.persistence.entity.BrandJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class BrandPersistenceMapper {
  public Brand toDomain(BrandJpaEntity entity) {
    return new Brand(
        BrandId.from(entity.getId()),
        entity.getName(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public BrandJpaEntity toJpaEntity(Brand brand) {
    return BrandJpaEntity.builder()
        .id(brand.getId() != null ? brand.getId().value() : null)
        .name(brand.getName())
        .createdAt(brand.getCreatedAt())
        .updatedAt(brand.getUpdatedAt())
        .build();
  }

}
