package org.josiasguerrero.products.application.usecase.Product;

import java.util.Map;

import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.PropertyValue;
import org.josiasguerrero.products.domain.valueobject.VariantAttribute;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateProductPropertiesUseCase {

  private final ProductRepository productRepository;
  private final PropertyRepository propertyRepository;
  private final ProductApplicationMapper productApplicationMapper;

  public ProductResponse exceute(String productId, Map<String, String> properties) {
    ProductId id = ProductId.from(productId);
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    ProductVariant defaultVariant = product.getVariants().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("Product has no variants"));

    defaultVariant.clearProperties();

    properties.forEach((propName, value) -> {
      PropertyId propId = findOrCreateProperty(propName);
      defaultVariant.addProperty(new VariantAttribute(propId, PropertyValue.of(value)));
    });

    productRepository.save(product);

    return productApplicationMapper.toResponse(product);
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
