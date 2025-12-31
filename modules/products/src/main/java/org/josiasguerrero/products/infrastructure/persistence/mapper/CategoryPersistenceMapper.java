package org.josiasguerrero.products.infrastructure.persistence.mapper;

import org.josiasguerrero.products.domain.entity.Category;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryPersistenceMapper {

  public Category toDomain(CategoryJpaEntity entity) {
    return new Category(
        CategoryId.from(entity.getId()),
        entity.getName(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public CategoryJpaEntity toJpaEntity(Category domain) {
    return CategoryJpaEntity.builder()
        .id(domain.getId() != null ? domain.getId().value() : null)
        .name(domain.getName())
        .description(domain.getDescription())
        .createdAt(domain.getCreatedAt())
        .updatedAt(domain.getUpdatedAt())
        .build();
  }
}
