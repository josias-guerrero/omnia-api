package org.josiasguerrero.products.domain.port;

import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.valueobject.PropertyId;

public class PropertyDomainService {
  private final PropertyRepository propertyRepository;

  public PropertyDomainService(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  public PropertyId findOrCreateProperty(String name) {
    return propertyRepository
        .findByName(name)
        .map(Property::getId)
        .orElseGet(
            () -> {
              Property newProperty = new Property(name);
              Property saved = propertyRepository.save(newProperty);
              return saved.getId();
            });
  }
}
