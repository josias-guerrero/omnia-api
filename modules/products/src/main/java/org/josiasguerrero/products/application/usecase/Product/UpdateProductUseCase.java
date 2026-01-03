package org.josiasguerrero.products.application.usecase.Product;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.josiasguerrero.products.application.dto.request.UpdateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.exception.CategoryNotFoundException;
import org.josiasguerrero.products.domain.exception.DuplicateSkuException;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.PropertyValue;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.josiasguerrero.shared.domain.valueobject.Money;
import org.josiasguerrero.products.domain.valueobject.Barcode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateProductUseCase {

  private final ProductRepository productRepository;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final PropertyRepository propertyRepository;
  private final DtoValidator dtoValidator;
  private final ProductApplicationMapper productApplicationMapper;

  public ProductResponse execute(String productId, UpdateProductRequest request) {
    dtoValidator.validate(request);

    ProductId id = ProductId.from(productId);
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    validateBusinessRules(product, request);
    updateProductFields(product, request);
    updateCategories(product, request.categoryIds());
    updateProperties(product, request.properties());

    productRepository.save(product);

    return productApplicationMapper.toResponse(product);
  }

  private void updateCategories(Product product, Set<Integer> categoryIds) {
    if (categoryIds == null)
      return;

    validateAllCategoriesExist(categoryIds);

    // Limpia las categorías actuales y agrega las nuevas
    product.clearCategories();
    categoryIds.forEach(catId -> product.assignToCategory(CategoryId.from(catId)));
  }

  private void updateProperties(Product product, Map<String, String> newPropertiesRequest) {
    if (newPropertiesRequest == null || newPropertiesRequest.isEmpty()) {
      return;
    }

    Map<PropertyId, PropertyValue> currentProperties = product.getProperties();

    // Convertir el request a Map<PropertyId, PropertyValue>
    Map<PropertyId, PropertyValue> newProperties = new HashMap<>();
    newPropertiesRequest.forEach((propName, value) -> {
      PropertyId propId = findOrCreateProperty(propName);
      newProperties.put(propId, PropertyValue.of(value));
    });

    // 1. Identificar propiedades a eliminar (están en current pero NO en new)
    Set<PropertyId> toRemove = new HashSet<>(currentProperties.keySet());
    toRemove.removeAll(newProperties.keySet());

    // 2. Eliminar las propiedades que ya no están
    toRemove.forEach(product::removeProperty);

    // 3. Agregar o actualizar propiedades
    newProperties.forEach((propId, propValue) -> {
      PropertyValue currentValue = currentProperties.get(propId);

      // Solo actualiza si es nueva O si el valor cambió
      if (currentValue == null || !currentValue.equals(propValue)) {
        product.addProperty(propId, propValue);
      }
    });
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

  private void validateAllCategoriesExist(Set<Integer> categoryIds) {
    for (Integer catId : categoryIds) {
      CategoryId categoryId = CategoryId.from(catId);
      categoryRepository.findById(categoryId)
          .orElseThrow(() -> new CategoryNotFoundException(
              "Category not found: " + catId));
    }
  }

  private void validateBusinessRules(Product product, UpdateProductRequest request) {
    if (request.sku() != null && !request.sku().isBlank()) {
      Sku newSku = Sku.from(request.sku());

      if (!product.getSku().equals(newSku)) {
        if (productRepository.existsBySku(newSku)) {
          throw new DuplicateSkuException(newSku);
        }
      }
    }

    // Validar que la marca exista (si viene)
    if (request.brandId() != null && !request.brandId().isBlank()) {
      BrandId brandId = BrandId.from(request.brandId());
      brandRepository.findById(brandId)
          .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));
    }

    // Validar que price > cost (si vienen ambos o uno de los dos)
    if (request.cost() != null || request.price() != null) {
      Money newCost = request.cost() != null
          ? new Money(request.cost())
          : product.getCost();

      Money newPrice = request.price() != null
          ? new Money(request.price())
          : product.getPrice();

      if (!newPrice.isGreaterThan(newCost)) {
        throw new IllegalArgumentException("Price must be greater than cost");
      }
    }
  }

  private void updateProductFields(Product product, UpdateProductRequest request) {

    if (request.sku() != null && !request.sku().isBlank()) {
      product.changeSku(Sku.from(request.sku()));
    }

    if (request.name() != null && !request.name().isBlank()) {
      product.rename(request.name());
    }

    if (request.description() != null) {
      product.changeDescription(request.description());
    }

    if (request.barcode() != null && !request.barcode().isBlank()) {
      product.setBarcode(new Barcode(request.barcode()));
    }

    if (request.cost() != null || request.price() != null) {
      Money newCost = request.cost() != null ? new Money(request.cost()) : product.getCost();
      Money newPrice = request.price() != null ? new Money(request.price()) : product.getPrice();
      product.updatePricing(newCost, newPrice);
    }

    if (request.stock() != null) {
      product.adjustStock(request.stock());
    }

    if (request.brandId() != null && !request.brandId().isBlank()) {
      product.assignToBrand(BrandId.from(request.brandId()));
    }
  }

}
