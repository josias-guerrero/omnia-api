package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.exception.BrandNotFoundException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.infrastructure.persistence.entity.BrandJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.mapper.BrandPersistenceMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {
  private BrandJpaRepository repository;
  private BrandPersistenceMapper mapper;

  @Override
  @Transactional
  public Brand save(Brand brand) {
    BrandJpaEntity entity = mapper.toJpaEntity(brand);
    BrandJpaEntity savedEntity = repository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Brand> findById(BrandId id) {
    return repository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Brand> findByName(String name) {
    return repository.findByName(name).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Brand> findAll() {
    return repository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByName(String name) {
    return repository.existsByName(name);
  }

  @Override
  @Transactional
  public void delete(BrandId id) {
    if (!repository.existsById(id.value())) {
      throw new BrandNotFoundException(id);
    }

    repository.deleteById(id.value());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasProducts(BrandId id) {
    return repository.hasProducts(id.value());
  }

}
