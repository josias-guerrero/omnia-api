package org.josiasguerrero.products.application.usecase.Product;

import java.util.HashSet;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.josiasguerrero.products.application.dto.request.CreateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
import org.josiasguerrero.products.domain.exception.DuplicateSkuException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyDomainService;
import org.josiasguerrero.products.domain.port.SkuGeneratorPort;
import org.josiasguerrero.products.domain.valueobject.Barcode;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.ProductVariantId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.PropertyValue;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.products.domain.valueobject.Stock;
import org.josiasguerrero.products.domain.valueobject.VariantAttribute;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.josiasguerrero.shared.domain.valueobject.Money;

@AllArgsConstructor
public class CreateProductUseCase {
  private final PropertyDomainService propertyDomainService;
  private final ProductRepository productRepository;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final DtoValidator dtoValidator;
  private final ProductApplicationMapper productApplicationMapper;
  private final SkuGeneratorPort skuGenerator;

  public ProductResponse execute(CreateProductRequest request) {
    dtoValidator.validate(request);
    validateBusinessRules(request);

    Product product = createProductEntity(request);
    assignRelations(product, request);
    Product newProduct = productRepository.save(product);

    return productApplicationMapper.toResponse(newProduct);
  }

  private void validateBusinessRules(CreateProductRequest request) {
    if (request.sku() != null) {
      Sku sku = Sku.from(request.sku());
      if (productRepository.existsBySku(sku)) {
        throw new DuplicateSkuException(sku);
      }
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
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
      }
    }
  }

  private Product createProductEntity(CreateProductRequest request) {
    ProductId id = ProductId.generate();
    Product product = new Product(id, request.name(), request.description());
    Sku sku;
    String brandName = null;

    if (request.brandId() != null) {
      BrandId brandId = BrandId.from(request.brandId());
      brandName = brandRepository.findById(brandId).get().getName();
      product.assignToBrand(brandId);
    }

    ProductVariantId variantId = ProductVariantId.generate();

    if (request.sku() == null) {
      Map<String, String> props =
          request.properties() != null ? request.properties() : Map.of();
      sku = generateUniqueSku(product, props, brandName);
    } else {
      sku = Sku.from(request.sku());
    }

    Barcode barcode =
        request.barcode() != null && !request.barcode().isBlank()
            ? new Barcode(request.barcode())
            : null;
    Stock stock = request.stock() != null ? new Stock(request.stock()) : Stock.empty();
    Money cost = new Money(request.cost());
    Money price = new Money(request.price());

    ProductVariant defaultVariant =
        new ProductVariant(variantId, sku, barcode, stock, cost, price, new HashSet<>());
    product.getVariants().add(defaultVariant);

    return product;
  }

  private Sku generateUniqueSku(Product product, Map<String, String> properties, String brandName) {
    Sku base = skuGenerator.generateSku(product, properties, brandName);
    if (!productRepository.existsBySku(base)) {
      return base;
    }
    for (int i = 1; i <= 999; i++) {
      Sku candidate = Sku.from(base.value() + "-" + i);
      if (!productRepository.existsBySku(candidate)) {
        return candidate;
      }
    }
    throw new IllegalStateException("Could not generate unique SKU after 999 attempts");
  }

  private void assignRelations(Product product, CreateProductRequest request) {
    // Asignar categorías
    if (request.categoryIds() != null) {
      request.categoryIds().forEach(catId -> product.assignToCategory(CategoryId.from(catId)));
    }

    // Asignar propiedades a la primera variante
    if (request.properties() != null && !product.getVariants().isEmpty()) {
      ProductVariant defaultVariant = product.getVariants().iterator().next();
      request
          .properties()
          .forEach(
              (propName, value) -> {
                PropertyId propId = propertyDomainService.findOrCreateProperty(propName);
                defaultVariant.addProperty(new VariantAttribute(propId, PropertyValue.of(value)));
              });
    }
  }
}
