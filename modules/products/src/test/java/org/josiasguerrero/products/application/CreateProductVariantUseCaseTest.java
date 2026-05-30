package org.josiasguerrero.products.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.josiasguerrero.products.application.dto.request.CreateProductVariantRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.application.usecase.Product.CreateProductVariantUseCase;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
import org.josiasguerrero.products.domain.exception.DuplicateSkuException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyDomainService;
import org.josiasguerrero.products.domain.port.SkuGeneratorPort;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductVariantUseCase")
class CreateProductVariantUseCaseTest {

  @Mock private ProductRepository productRepository;
  @Mock private PropertyDomainService propertyDomainService;
  @Mock private DtoValidator dtoValidator;
  @Mock private ProductApplicationMapper productApplicationMapper;
  @Mock private SkuGeneratorPort skuGenerator;
  @Mock private BrandRepository brandRepository;

  @InjectMocks private CreateProductVariantUseCase createProductVariantUseCase;

  @Test
  @DisplayName("Should create product variant successfully")
  void should_create_variant_successfully() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Product Name", "Product Description");
    CreateProductVariantRequest request =
        new CreateProductVariantRequest(
            "SKU-NEW",
            "123456789012",
            new BigDecimal("10.0"),
            new BigDecimal("15.0"),
            50,
            Map.of("color", "red"));

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.existsBySku(Sku.from("SKU-NEW"))).thenReturn(false);
    when(propertyDomainService.findOrCreateProperty("color")).thenReturn(PropertyId.from(1));
    when(productApplicationMapper.toResponse(product))
        .thenReturn(
            new ProductResponse(
                productId.value().toString(),
                "Product Name",
                "Product Description",
                null,
                Set.of(),
                Set.of(),
                LocalDateTime.now(),
                LocalDateTime.now()));

    ProductResponse response =
        createProductVariantUseCase.execute(productId.value().toString(), request);

    assertNotNull(response);
    assertEquals(1, product.getVariants().size());
    ProductVariant variant = product.getVariants().iterator().next();
    assertEquals("SKU-NEW", variant.getSku().value());
    assertEquals("123456789012", variant.getBarcode().value());
    assertEquals(50, variant.getStock().quantity());
    assertEquals(new BigDecimal("10.00"), variant.getCost().amount());
    assertEquals(new BigDecimal("15.00"), variant.getPrice().amount());

    verify(dtoValidator).validate(request);
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("Should throw DuplicateSkuException when SKU already exists")
  void should_throw_duplicate_sku_exception() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Product Name", "Product Description");
    CreateProductVariantRequest request =
        new CreateProductVariantRequest(
            "SKU-DUP", null, new BigDecimal("10.0"), new BigDecimal("15.0"), 50, null);

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.existsBySku(Sku.from("SKU-DUP"))).thenReturn(true);

    assertThrows(
        DuplicateSkuException.class,
        () -> {
          createProductVariantUseCase.execute(productId.value().toString(), request);
        });

    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when price is less than cost")
  void should_throw_pricing_exception() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Product Name", "Product Description");
    CreateProductVariantRequest request =
        new CreateProductVariantRequest(
            "SKU-NEW", null, new BigDecimal("15.0"), new BigDecimal("10.0"), 50, null);

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.existsBySku(Sku.from("SKU-NEW"))).thenReturn(false);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          createProductVariantUseCase.execute(productId.value().toString(), request);
        });

    verify(productRepository, never()).save(any(Product.class));
  }
}
