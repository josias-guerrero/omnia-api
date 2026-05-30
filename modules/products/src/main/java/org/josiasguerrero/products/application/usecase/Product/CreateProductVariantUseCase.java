package org.josiasguerrero.products.application.usecase.Product;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.josiasguerrero.products.application.dto.request.CreateProductVariantRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
import org.josiasguerrero.products.domain.exception.DuplicateSkuException;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyDomainService;
import org.josiasguerrero.products.domain.port.SkuGeneratorPort;
import org.josiasguerrero.products.domain.valueobject.Barcode;
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
public class CreateProductVariantUseCase {
  private final PropertyDomainService propertyDomainService;
  private final ProductRepository productRepository;
  private final DtoValidator dtoValidator;
  private final ProductApplicationMapper productApplicationMapper;
  private final SkuGeneratorPort skuGenerator;
  private final BrandRepository brandRepository;

  public ProductResponse execute(String productId, CreateProductVariantRequest request) {
    dtoValidator.validate(request);

    ProductId id = ProductId.from(productId);
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    Sku sku;
    if (request.sku() == null) {
      String brandName = product.getBrandId() != null
          ? brandRepository.findById(product.getBrandId()).get().getName()
          : null;
      Map<String, String> props =
          request.properties() != null ? request.properties() : Map.of();
      sku = generateUniqueSku(product, props, brandName);
    } else {
      sku = Sku.from(request.sku());
      if (productRepository.existsBySku(sku)) {
        throw new DuplicateSkuException(sku);
      }
    }

    ProductVariantId variantId = ProductVariantId.generate();
    Barcode barcode =
        request.barcode() != null && !request.barcode().isBlank()
            ? new Barcode(request.barcode())
            : null;
    Stock stock = request.stock() != null ? new Stock(request.stock()) : Stock.empty();
    Money cost = new Money(request.cost());
    Money price = new Money(request.price());

    Set<VariantAttribute> attributes = new HashSet<>();
    if (request.properties() != null) {
      request
          .properties()
          .forEach(
              (propName, value) -> {
                PropertyId propId = propertyDomainService.findOrCreateProperty(propName);
                attributes.add(new VariantAttribute(propId, PropertyValue.of(value)));
              });
    }

    ProductVariant newVariant =
        new ProductVariant(variantId, sku, barcode, stock, cost, price, attributes);
    product.addVariant(newVariant);

    productRepository.save(product);

    return productApplicationMapper.toResponse(product);
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
}
