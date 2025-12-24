package org.josiasguerrero.products.application.usecase.Product;

import java.util.Map;

import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.PropertyValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateProductPropertiesUseCase {

  private final ProductRepository productRepository;
  private final PropertyRepository propertyRepository;

  public ProductResponse exceute(String productId, Map<String, String> properties) {
    ProductId id = ProductId.from(productId);
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    properties.forEach((propName, value) -> {
      PropertyId propId = findOrCreateProperty(propName);
      product.addProperty(propId, PropertyValue.of(value));
    });

    productRepository.save(product);

    return ProductApplicationMapper.toResponse(product);
  }

  private PropertyId findOrCreateProperty(String name) {
    return propertyRepository.findByName(name)
        .map(Property::getId)
        .orElseGet(() -> {
          Property newProperty = new Property(name);
          propertyRepository.save(newProperty);
          return newProperty.getId();
        });

  }
}
