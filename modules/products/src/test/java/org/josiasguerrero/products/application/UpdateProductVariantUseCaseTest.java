package org.josiasguerrero.products.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.josiasguerrero.products.application.dto.request.UpdateProductVariantRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductVariantUseCase;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
import org.josiasguerrero.products.domain.exception.DuplicateSkuException;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyDomainService;
import org.josiasguerrero.products.domain.valueobject.Barcode;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.ProductVariantId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.products.domain.valueobject.Stock;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.josiasguerrero.shared.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductVariantUseCase")
class UpdateProductVariantUseCaseTest {

  @Mock
  private ProductRepository productRepository;
  @Mock
  private PropertyDomainService propertyDomainService;
  @Mock
  private DtoValidator dtoValidator;
  @Mock
  private ProductApplicationMapper productApplicationMapper;

  @InjectMocks
  private UpdateProductVariantUseCase updateProductVariantUseCase;

  @Test
  @DisplayName("Should update product variant successfully")
  void should_update_variant_successfully() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Product Name", "Product Description");
    
    ProductVariantId variantId = ProductVariantId.generate();
    ProductVariant variant = new ProductVariant(
        variantId, Sku.from("SKU-OLD"), new Barcode("111111111111"), Stock.of(10), new Money(new BigDecimal("5.0")), new Money(new BigDecimal("10.0")), new HashSet<>()
    );
    product.getVariants().add(variant);

    UpdateProductVariantRequest request = new UpdateProductVariantRequest(
        "SKU-NEW", "222222222222", new BigDecimal("12.0"), new BigDecimal("18.0"), 20, Map.of("size", "L")
    );

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.existsBySku(Sku.from("SKU-NEW"))).thenReturn(false);
    when(propertyDomainService.findOrCreateProperty("size")).thenReturn(PropertyId.from(2));
    when(productApplicationMapper.toResponse(product)).thenReturn(
        new ProductResponse(productId.value().toString(), "Product Name", "Product Description", null, Set.of(), Set.of(), LocalDateTime.now(), LocalDateTime.now())
    );

    ProductResponse response = updateProductVariantUseCase.execute(productId.value().toString(), variantId.value().toString(), request);

    assertNotNull(response);
    assertEquals("SKU-NEW", variant.getSku().value());
    assertEquals("222222222222", variant.getBarcode().value());
    assertEquals(20, variant.getStock().quantity());
    assertEquals(new BigDecimal("12.00"), variant.getCost().amount());
    assertEquals(new BigDecimal("18.00"), variant.getPrice().amount());

    verify(dtoValidator).validate(request);
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("Should allow keeping same SKU without duplicate check failure")
  void should_allow_same_sku() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Product Name", "Product Description");
    
    ProductVariantId variantId = ProductVariantId.generate();
    ProductVariant variant = new ProductVariant(
        variantId, Sku.from("SKU-SAME"), null, Stock.of(10), new Money(new BigDecimal("5.0")), new Money(new BigDecimal("10.0")), new HashSet<>()
    );
    product.getVariants().add(variant);

    UpdateProductVariantRequest request = new UpdateProductVariantRequest(
        "SKU-SAME", null, null, null, null, null
    );

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productApplicationMapper.toResponse(product)).thenReturn(
        new ProductResponse(productId.value().toString(), "Product Name", "Product Description", null, Set.of(), Set.of(), LocalDateTime.now(), LocalDateTime.now())
    );

    updateProductVariantUseCase.execute(productId.value().toString(), variantId.value().toString(), request);

    verify(productRepository, never()).existsBySku(any(Sku.class));
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("Should throw DuplicateSkuException when updating SKU to an existing SKU of another product")
  void should_throw_duplicate_sku_exception() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Product Name", "Product Description");
    
    ProductVariantId variantId = ProductVariantId.generate();
    ProductVariant variant = new ProductVariant(
        variantId, Sku.from("SKU-OLD"), null, Stock.of(10), new Money(new BigDecimal("5.0")), new Money(new BigDecimal("10.0")), new HashSet<>()
    );
    product.getVariants().add(variant);

    UpdateProductVariantRequest request = new UpdateProductVariantRequest(
        "SKU-EXISTING", null, null, null, null, null
    );

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.existsBySku(Sku.from("SKU-EXISTING"))).thenReturn(true);

    assertThrows(DuplicateSkuException.class, () -> {
      updateProductVariantUseCase.execute(productId.value().toString(), variantId.value().toString(), request);
    });

    verify(productRepository, never()).save(any(Product.class));
  }
}
