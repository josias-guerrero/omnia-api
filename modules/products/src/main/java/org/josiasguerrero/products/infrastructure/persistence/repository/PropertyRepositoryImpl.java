package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.exception.PropertyNotFoundException;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.infrastructure.persistence.entity.PropertyJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.mapper.PropertyPersistenceMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PropertyRepositoryImpl implements PropertyRepository {
  private PropertyJpaRepository jpaRepository;
  private PropertyPersistenceMapper mapper;

  @Override
  @Transactional
  public Property save(Property property) {
    PropertyJpaEntity entity = mapper.toJpaEntity(property);
    var savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Property> findById(PropertyId id) {
    return jpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Property> findByName(String name) {
    return jpaRepository.findByName(name).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Property> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByName(String name) {
    return jpaRepository.existsByName(name);
  }

  @Override
  @Transactional
  public void delete(PropertyId id) {
    if (!jpaRepository.existsById(id.value())) {
      throw new PropertyNotFoundException(id);
    }

    jpaRepository.deleteById(id.value());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isUsedByProducts(PropertyId id) {
    return jpaRepository.isUsedByProducts(id.value());
  }

}
