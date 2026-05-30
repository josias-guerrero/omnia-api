package org.josiasguerrero.products.application.usecase.Product;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.josiasguerrero.products.application.dto.request.CreateProductVariantRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
import org.josiasguerrero.products.domain.exception.DuplicateSkuException;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyDomainService;
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

  public ProductResponse execute(String productId, CreateProductVariantRequest request) {
    dtoValidator.validate(request);

    ProductId id = ProductId.from(productId);
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    Sku sku = Sku.from(request.sku());
    if (productRepository.existsBySku(sku)) {
      throw new DuplicateSkuException(sku);
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
}
