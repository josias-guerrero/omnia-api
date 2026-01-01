package org.josiasguerrero.products.infrastructure.persistence.mapper;

import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.infrastructure.persistence.entity.PropertyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PropertyPersistenceMapper {

  public Property toDomain(PropertyJpaEntity entity) {
    return new Property(
        PropertyId.from(entity.getId()),
        entity.getName(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public PropertyJpaEntity toJpaEntity(Property property) {
    return PropertyJpaEntity.builder()
        .id(property.getId() != null ? property.getId().value() : null)
        .name(property.getName())
        .createdAt(property.getCreatedAt())
        .updatedAt(property.getUpdatedAt())
        .build();
  }
}
