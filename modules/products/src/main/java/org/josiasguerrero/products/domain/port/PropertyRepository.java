package org.josiasguerrero.products.domain.port;

import java.util.List;
import java.util.Optional;

import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.valueobject.PropertyId;

public interface PropertyRepository {

  Property save(Property property);

  Optional<Property> findById(PropertyId id);

  Optional<Property> findByName(String name);

  List<Property> findAll();

  boolean existsByName(String name);

  void delete(PropertyId id);

  // Validación de negocio
  boolean isUsedByProducts(PropertyId id);
}
