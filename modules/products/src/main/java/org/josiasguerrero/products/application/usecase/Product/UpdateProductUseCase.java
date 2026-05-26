package org.josiasguerrero.products.application.usecase.Product;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

import org.josiasguerrero.products.application.dto.request.UpdateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
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
import org.josiasguerrero.products.domain.valueobject.VariantAttribute;
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

    product.clearCategories();
    categoryIds.forEach(catId -> product.assignToCategory(CategoryId.from(catId)));
  }

  private void updateProperties(Product product, Map<String, String> newPropertiesRequest) {
    if (newPropertiesRequest == null || newPropertiesRequest.isEmpty()) {
      return;
    }

    ProductVariant defaultVariant = product.getVariants().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("Product has no variants"));

    Set<VariantAttribute> currentProperties = defaultVariant.getProperties();

    Map<PropertyId, PropertyValue> newProperties = new HashMap<>();
    newPropertiesRequest.forEach((propName, value) -> {
      PropertyId propId = findOrCreateProperty(propName);
      newProperties.put(propId, PropertyValue.of(value));
    });

    Set<PropertyId> currentPropertyIds = currentProperties.stream()
        .map(VariantAttribute::propertyId)
        .collect(Collectors.toSet());

    Set<PropertyId> toRemove = new HashSet<>(currentPropertyIds);
    toRemove.removeAll(newProperties.keySet());

    toRemove.forEach(defaultVariant::removeProperty);

    newProperties.forEach((propId, propValue) -> {
      Optional<VariantAttribute> currentAttr = currentProperties.stream()
          .filter(attr -> attr.propertyId().equals(propId))
          .findFirst();

      if (currentAttr.isEmpty() || !currentAttr.get().value().equals(propValue)) {
        if (currentAttr.isPresent()) {
          defaultVariant.removeProperty(propId);
        }
        defaultVariant.addProperty(new VariantAttribute(propId, propValue));
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
    ProductVariant defaultVariant = product.getVariants().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("Product has no variants"));

    if (request.sku() != null && !request.sku().isBlank()) {
      Sku newSku = Sku.from(request.sku());

      if (!defaultVariant.getSku().equals(newSku)) {
        if (productRepository.existsBySku(newSku)) {
          throw new DuplicateSkuException(newSku);
        }
      }
    }

    if (request.brandId() != null && !request.brandId().isBlank()) {
      BrandId brandId = BrandId.from(request.brandId());
      brandRepository.findById(brandId)
          .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));
    }

    if (request.cost() != null || request.price() != null) {
      Money newCost = request.cost() != null
          ? new Money(request.cost())
          : defaultVariant.getCost();

      Money newPrice = request.price() != null
          ? new Money(request.price())
          : defaultVariant.getPrice();

      if (!newPrice.isGreaterThan(newCost)) {
        throw new IllegalArgumentException("Price must be greater than cost");
      }
    }
  }

  private void updateProductFields(Product product, UpdateProductRequest request) {
    ProductVariant defaultVariant = product.getVariants().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("Product has no variants"));

    if (request.sku() != null && !request.sku().isBlank()) {
      defaultVariant.changeSku(Sku.from(request.sku()));
    }

    if (request.name() != null && !request.name().isBlank()) {
      product.rename(request.name());
    }

    if (request.description() != null) {
      product.changeDescription(request.description());
    }

    if (request.barcode() != null && !request.barcode().isBlank()) {
      defaultVariant.setBarcode(new Barcode(request.barcode()));
    }

    if (request.cost() != null || request.price() != null) {
      Money newCost = request.cost() != null ? new Money(request.cost()) : defaultVariant.getCost();
      Money newPrice = request.price() != null ? new Money(request.price()) : defaultVariant.getPrice();
      defaultVariant.updatePricing(newCost, newPrice);
    }

    if (request.stock() != null) {
      defaultVariant.adjustStock(request.stock());
    }

    if (request.brandId() != null && !request.brandId().isBlank()) {
      product.assignToBrand(BrandId.from(request.brandId()));
    }
  }

}
