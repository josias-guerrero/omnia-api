package org.josiasguerrero.products.domain.port;

import java.util.List;
import java.util.Optional;

import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.valueobject.BrandId;

public interface BrandRepository {

  Brand save(Brand brand);

  Optional<Brand> findById(BrandId id);

  Optional<Brand> findByName(String name);

  List<Brand> findAll();

  boolean existsByName(String name);

  void delete(BrandId id);

  // Validación de negocio
  boolean hasProducts(BrandId id);
}
