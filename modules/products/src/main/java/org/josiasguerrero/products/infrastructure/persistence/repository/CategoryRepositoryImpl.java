package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.josiasguerrero.products.domain.entity.Category;
import org.josiasguerrero.products.domain.exception.CategoryNotFoundException;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.infrastructure.persistence.entity.CategoryJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CategoryRepositoryImpl implements CategoryRepository {
  private CategoryJpaRepository jpaRepository;
  private CategoryPersistenceMapper mapper;

  @Override
  @Transactional
  public Category save(Category category) {
    CategoryJpaEntity entity = mapper.toJpaEntity(category);
    var savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Category> findById(CategoryId id) {
    return jpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Category> findByName(String name) {
    return jpaRepository.findByName(name).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Category> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByName(String name) {
    return jpaRepository.existsByName(name);
  }

  @Override
  @Transactional
  public void delete(CategoryId id) {
    if (!jpaRepository.existsById(id.value())) {
      throw new CategoryNotFoundException(id);
    }
    jpaRepository.deleteById(id.value());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasProducts(CategoryId id) {
    return jpaRepository.hasProducts(id.value());
  }

}
