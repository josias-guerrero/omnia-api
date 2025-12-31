package org.josiasguerrero.products.application.usecase.Product;

import org.josiasguerrero.products.application.dto.request.CreateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.exception.DuplicateSkuException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.valueobject.Barcode;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.PropertyValue;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.josiasguerrero.shared.domain.valueobject.Money;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateProductUseCase {
  private final ProductRepository productRepository;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final PropertyRepository propertyRepository;
  private final DtoValidator dtoValidator;

  public ProductResponse execute(CreateProductRequest request) {
    dtoValidator.validate(request);
    validateBusinessRules(request);

    Product product = createProductEntity(request);
    assignRelations(product, request);
    productRepository.save(product);

    return ProductApplicationMapper.toResponse(product);

  }

  private void validateBusinessRules(CreateProductRequest request) {
    Sku sku = Sku.from(request.sku());
    if (productRepository.existsBySku(sku)) {
      throw new DuplicateSkuException(sku);
    }

    if (request.brandId() != null) {
      BrandId brandId = BrandId.from(request.brandId());
      if (!brandRepository.findById(brandId).isPresent()) {
        throw new IllegalArgumentException("Brand not found: " + brandId);
      }
    }

    if (request.categoryIds() != null && !request.categoryIds().isEmpty()) {
      for (Integer catId : request.categoryIds()) {
        CategoryId categoryId = CategoryId.from(catId);
        categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
      }
    }
  }

  private Product createProductEntity(CreateProductRequest request) {
    ProductId id = ProductId.generate();
    Sku sku = Sku.from(request.sku());
    Money cost = new Money(request.cost());
    Money price = new Money(request.price());

    Product product = new Product(id, sku, request.name(), request.description(), cost, price);

    if (request.barcode() != null && !request.barcode().isBlank()) {
      product.setBarcode(new Barcode(request.barcode()));
    }

    if (request.stock() != null && request.stock() > 0) {
      product.adjustStock(request.stock());
    }

    if (request.brandId() != null) {
      product.assignToBrand(BrandId.from(request.brandId()));
    }

    return product;
  }

  private void assignRelations(Product product, CreateProductRequest request) {
    // Asignar categorías
    if (request.categoryIds() != null) {
      request.categoryIds().forEach(catId -> product.assignToCategory(CategoryId.from(catId)));
    }

    // Asignar propiedades
    if (request.properties() != null) {
      request.properties().forEach((propName, value) -> {
        PropertyId propId = findOrCreateProperty(propName);
        product.addProperty(propId, PropertyValue.of(value));
      });
    }
  }

  private PropertyId findOrCreateProperty(String name) {
    return propertyRepository.findByName(name).map(Property::getId).orElseGet(() -> {
      Property newProperty = new Property(name);
      propertyRepository.save(newProperty);
      return newProperty.getId();
    });
  }
}
