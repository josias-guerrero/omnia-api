package org.josiasguerrero.products.application.usecase.Product;

import lombok.AllArgsConstructor;
import org.josiasguerrero.products.application.dto.request.UpdateProductVariantRequest;
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
import org.josiasguerrero.products.domain.valueobject.VariantAttribute;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.josiasguerrero.shared.domain.valueobject.Money;

@AllArgsConstructor
public class UpdateProductVariantUseCase {

  private final ProductRepository productRepository;
  private final DtoValidator dtoValidator;
  private final PropertyDomainService propertyDomainService;
  private final ProductApplicationMapper productApplicationMapper;

  public ProductResponse execute(
      String productId, String variantId, UpdateProductVariantRequest request) {
    dtoValidator.validate(request);

    ProductId id = ProductId.from(productId);
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    ProductVariantId varId = ProductVariantId.from(variantId);
    ProductVariant variant =
        product.getVariants().stream()
            .filter(v -> v.getId().equals(varId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + variantId));

    validateBusinessRules(variant, request);
    updateVariantFields(variant, request);

    productRepository.save(product);

    return productApplicationMapper.toResponse(product);
  }

  private void validateBusinessRules(ProductVariant variant, UpdateProductVariantRequest request) {
    if (request.sku() != null && !request.sku().isBlank()) {
      Sku newSku = Sku.from(request.sku());
      if (!variant.getSku().equals(newSku)) {
        if (productRepository.existsBySku(newSku)) {
          throw new DuplicateSkuException(newSku);
        }
      }
    }

    if (request.cost() != null || request.price() != null) {
      Money newCost = request.cost() != null ? new Money(request.cost()) : variant.getCost();
      Money newPrice = request.price() != null ? new Money(request.price()) : variant.getPrice();
      if (!newPrice.isGreaterThan(newCost)) {
        throw new IllegalArgumentException("Price must be greater than cost");
      }
    }
  }

  private void updateVariantFields(ProductVariant variant, UpdateProductVariantRequest request) {
    if (request.sku() != null && !request.sku().isBlank()) {
      variant.changeSku(Sku.from(request.sku()));
    }

    if (request.barcode() != null) {
      variant.setBarcode(request.barcode().isBlank() ? null : new Barcode(request.barcode()));
    }

    if (request.cost() != null || request.price() != null) {
      Money newCost = request.cost() != null ? new Money(request.cost()) : variant.getCost();
      Money newPrice = request.price() != null ? new Money(request.price()) : variant.getPrice();
      variant.updatePricing(newCost, newPrice);
    }

    if (request.stock() != null) {
      variant.adjustStock(request.stock());
    }

    if (request.properties() != null) {
      variant.clearProperties();
      request
          .properties()
          .forEach(
              (propName, value) -> {
                PropertyId propId = propertyDomainService.findOrCreateProperty(propName);
                variant.addProperty(new VariantAttribute(propId, PropertyValue.of(value)));
              });
    }
  }
}
