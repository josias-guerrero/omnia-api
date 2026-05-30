package org.josiasguerrero.products.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import java.util.UUID;

import org.josiasguerrero.products.application.dto.request.CreateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.dto.response.ProductVariantResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.application.usecase.Product.CreateProductUseCase;
import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.entity.Category;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
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
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.products.domain.valueobject.Stock;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.josiasguerrero.shared.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductUseCase")
class CreateProductUseCaseTest {

  private static final String PRODUCT_NAME = "CUADERNO CUADRICULADO";
  private static final String PRODUCT_DESCRIPTION = "Cuaderno cuadriculado #3";
  private static final String PRODUCT_SKU = "CUAD-CUAD-3";
  private static final String PRODUCT_BARCODE = "123456789023";
  private static final BigDecimal PRODUCT_COST = new BigDecimal("0.7");
  private static final BigDecimal PRODUCT_PRICE = new BigDecimal("1.2");
  private static final int PRODUCT_STOCK = 25;

  @Mock
  private ProductRepository productRepository;
  @Mock
  private BrandRepository brandRepository;
  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private PropertyRepository propertyRepository;
  @Mock
  private DtoValidator dtoValidator;
  @Mock
  private ProductApplicationMapper productApplicationMapper;

  @InjectMocks
  private CreateProductUseCase createProductUseCase;

  @Captor
  private ArgumentCaptor<Product> productCaptor;

  private ProductResponse buildProductResponse(ProductId productId) {
    var now = LocalDateTime.now();
    var variantResponse = new ProductVariantResponse(
        UUID.randomUUID().toString(),
        PRODUCT_SKU,
        PRODUCT_BARCODE,
        PRODUCT_STOCK,
        PRODUCT_COST,
        PRODUCT_PRICE,
        new HashMap<>(),
        now,
        now
    );
    return new ProductResponse(
        productId.toString(),
        PRODUCT_NAME,
        PRODUCT_DESCRIPTION,
        null,
        Set.of(),
        Set.of(variantResponse),
        now,
        now);
  }

  private CreateProductRequest buildNewProductRequest(ProductOptionalData data) {
    return new CreateProductRequest(
        PRODUCT_SKU,
        PRODUCT_NAME,
        PRODUCT_COST,
        PRODUCT_PRICE,
        data.getStock() != null ? data.getStock() : PRODUCT_STOCK,
        PRODUCT_DESCRIPTION,
        data.getBarcode() != null ? data.getBarcode() : PRODUCT_BARCODE,
        data.getBrandId(),
        data.getCategories(),
        data.getProperties());
  }

  private void assertProductVariant(Product savedProduct, Sku expectedSku,
      Barcode expectedBarcode, Stock expectedStock,
      Money expectedCost, Money expectedPrice, int propertyCount) {
    assertEquals(1, savedProduct.getVariants().size(),
        "Product should have exactly one default variant");
    ProductVariant variant = savedProduct.getVariants().iterator().next();
    assertEquals(expectedSku, variant.getSku());
    assertEquals(expectedBarcode, variant.getBarcode());
    assertEquals(expectedStock, variant.getStock());
    assertEquals(expectedCost, variant.getCost());
    assertEquals(expectedPrice, variant.getPrice());
    assertEquals(propertyCount, variant.getProperties().size());
  }

  @Test
  @DisplayName("Should create product successfully with default variant")
  void should_create_product_successfully() {
    var request = buildNewProductRequest(ProductOptionalData.builder().build());
    var productId = ProductId.generate();
    var productEntity = new Product(productId, PRODUCT_NAME, PRODUCT_DESCRIPTION);
    var productResponse = buildProductResponse(productId);

    Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productEntity);
    Mockito.when(productApplicationMapper.toResponse(productEntity)).thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    assertEquals(PRODUCT_NAME, result.name());
    assertEquals(PRODUCT_SKU, result.variants().iterator().next().sku());
    assertEquals(PRODUCT_DESCRIPTION, result.description());
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(productEntity);

    Product savedProduct = productCaptor.getValue();
    assertEquals(PRODUCT_NAME, savedProduct.getName());
    assertEquals(PRODUCT_DESCRIPTION, savedProduct.getDescription());
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 0);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create product without brand")
  void should_create_product_without_brand() {
    var request = buildNewProductRequest(ProductOptionalData.builder().build());
    var productId = ProductId.generate();
    var productEntity = new Product(productId, PRODUCT_NAME, PRODUCT_DESCRIPTION);
    var productResponse = buildProductResponse(productId);

    Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productEntity);
    Mockito.when(productApplicationMapper.toResponse(productEntity)).thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    assertEquals(PRODUCT_NAME, result.name());
    assertNull(result.brand());
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(productEntity);

    Product savedProduct = productCaptor.getValue();
    assertNull(savedProduct.getBrandId());
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 0);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create product with multiple categories")
  void should_create_product_with_multiple_categories() {
    Set<Integer> categoryIds = Set.of(1, 2, 3);
    var request = buildNewProductRequest(
        ProductOptionalData.builder().categories(categoryIds).build());
    var productId = ProductId.generate();
    var productEntity = new Product(productId, PRODUCT_NAME, PRODUCT_DESCRIPTION);
    var productResponse = buildProductResponse(productId);

    Mockito.when(categoryRepository.findById(Mockito.any(CategoryId.class)))
        .thenReturn(Optional.of(Mockito.mock(Category.class)));
    Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productEntity);
    Mockito.when(productApplicationMapper.toResponse(productEntity)).thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    assertEquals(PRODUCT_NAME, result.name());
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(categoryRepository, Mockito.times(3))
        .findById(Mockito.any(CategoryId.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(productEntity);

    Product savedProduct = productCaptor.getValue();
    assertEquals(3, savedProduct.getCategoryIds().size());
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 0);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create product with brand, multiple categories and properties")
  void should_create_product_with_brand_categories_and_properties() {
    Integer brandId = 1;
    Set<Integer> categoryIds = Set.of(1, 2);
    Map<String, String> properties = Map.of("color", "rojo", "talla", "M");
    var request = buildNewProductRequest(
        ProductOptionalData.builder()
            .brandId(brandId)
            .categories(categoryIds)
            .properties(properties)
            .build());
    var productId = ProductId.generate();
    var productEntity = new Product(productId, PRODUCT_NAME, PRODUCT_DESCRIPTION);
    var productResponse = buildProductResponse(productId);

    Mockito.when(brandRepository.findById(Mockito.any(BrandId.class)))
        .thenReturn(Optional.of(new Brand(new BrandId(brandId), "Test Brand")));
    Mockito.when(categoryRepository.findById(Mockito.any(CategoryId.class)))
        .thenReturn(Optional.of(Mockito.mock(Category.class)));
    Mockito.when(propertyRepository.findByName("color"))
        .thenReturn(Optional.of(new Property(PropertyId.from(1), "color")));
    Mockito.when(propertyRepository.findByName("talla"))
        .thenReturn(Optional.of(new Property(PropertyId.from(2), "talla")));
    Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productEntity);
    Mockito.when(productApplicationMapper.toResponse(productEntity)).thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    assertEquals(PRODUCT_NAME, result.name());
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(brandRepository).findById(Mockito.any(BrandId.class));
    Mockito.verify(categoryRepository, Mockito.times(2))
        .findById(Mockito.any(CategoryId.class));
    Mockito.verify(propertyRepository).findByName("color");
    Mockito.verify(propertyRepository).findByName("talla");
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(productEntity);

    Product savedProduct = productCaptor.getValue();
    assertEquals(brandId, savedProduct.getBrandId().value());
    assertEquals(2, savedProduct.getCategoryIds().size());
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 2);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should assign existing property to product variant")
  void should_assign_existing_property() {
    PropertyId existingId = PropertyId.from(21);
    Property existingProperty = new Property(existingId, "color");
    var request = buildNewProductRequest(
        ProductOptionalData.builder().properties(Map.of("color", "rojo")).build());
    var productId = ProductId.generate();
    var productResponse = buildProductResponse(productId);

    Mockito.when(propertyRepository.findByName("color"))
        .thenReturn(Optional.of(existingProperty));
    Mockito.when(productRepository.save(Mockito.any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    Mockito.when(productApplicationMapper.toResponse(Mockito.any(Product.class)))
        .thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(propertyRepository).findByName("color");
    Mockito.verify(propertyRepository, Mockito.never()).save(Mockito.any(Property.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(Mockito.any(Product.class));

    Product savedProduct = productCaptor.getValue();
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 1);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create property when not found and assign to variant")
  void should_create_property_when_not_exists() {
    PropertyId newId = PropertyId.from(21);
    Property newProperty = new Property(newId, "color");
    var request = buildNewProductRequest(
        ProductOptionalData.builder().properties(Map.of("color", "rojo")).build());
    var productId = ProductId.generate();
    var productResponse = buildProductResponse(productId);

    Mockito.when(propertyRepository.findByName("color"))
        .thenReturn(Optional.empty());
    Mockito.when(propertyRepository.save(Mockito.any(Property.class)))
        .thenReturn(newProperty);
    Mockito.when(productRepository.existsBySku(Mockito.any(Sku.class)))
        .thenReturn(false);
    Mockito.when(productRepository.save(Mockito.any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    Mockito.when(productApplicationMapper.toResponse(Mockito.any(Product.class)))
        .thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(propertyRepository).findByName("color");
    Mockito.verify(propertyRepository).save(Mockito.any(Property.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(Mockito.any(Product.class));

    Product savedProduct = productCaptor.getValue();
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 1);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create product with multiple properties assigned to variant")
  void should_create_product_with_multiple_properties() {
    Map<String, String> properties = Map.of("color", "rojo", "size", "M", "weight", "150g");
    var request = buildNewProductRequest(
        ProductOptionalData.builder().properties(properties).build());
    var productId = ProductId.generate();
    var productResponse = buildProductResponse(productId);

    Mockito.when(propertyRepository.findByName("color"))
        .thenReturn(Optional.of(new Property(PropertyId.from(1), "color")));
    Mockito.when(propertyRepository.findByName("size"))
        .thenReturn(Optional.of(new Property(PropertyId.from(2), "size")));
    Mockito.when(propertyRepository.findByName("weight"))
        .thenReturn(Optional.of(new Property(PropertyId.from(3), "weight")));
    Mockito.when(productRepository.save(Mockito.any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    Mockito.when(productApplicationMapper.toResponse(Mockito.any(Product.class)))
        .thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(propertyRepository).findByName("color");
    Mockito.verify(propertyRepository).findByName("size");
    Mockito.verify(propertyRepository).findByName("weight");
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(Mockito.any(Product.class));

    Product savedProduct = productCaptor.getValue();
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 3);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create product with empty properties and categories")
  void should_create_product_with_empty_properties_and_categories() {
    var request = buildNewProductRequest(
        ProductOptionalData.builder().properties(Map.of()).categories(Set.of()).build());
    var productId = ProductId.generate();
    var productEntity = new Product(productId, PRODUCT_NAME, PRODUCT_DESCRIPTION);
    var productResponse = buildProductResponse(productId);

    Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productEntity);
    Mockito.when(productApplicationMapper.toResponse(productEntity)).thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    assertEquals(PRODUCT_NAME, result.name());
    assertTrue(result.variants().iterator().next().properties().isEmpty());
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(productEntity);

    Product savedProduct = productCaptor.getValue();
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 0);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create product with null barcode")
  void should_create_product_with_null_barcode() {
    var request = new CreateProductRequest(
        PRODUCT_SKU, PRODUCT_NAME, PRODUCT_COST, PRODUCT_PRICE, PRODUCT_STOCK,
        PRODUCT_DESCRIPTION, null, null, null, null);
    var productId = ProductId.generate();
    var productEntity = new Product(productId, PRODUCT_NAME, PRODUCT_DESCRIPTION);
    var productResponse = buildProductResponse(productId);

    Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productEntity);
    Mockito.when(productApplicationMapper.toResponse(productEntity)).thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(productEntity);

    Product savedProduct = productCaptor.getValue();
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        null, Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 0);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create product with blank barcode treated as null")
  void should_create_product_with_blank_barcode() {
    var request = new CreateProductRequest(
        PRODUCT_SKU, PRODUCT_NAME, PRODUCT_COST, PRODUCT_PRICE, PRODUCT_STOCK,
        PRODUCT_DESCRIPTION, "", null, null, null);
    var productId = ProductId.generate();
    var productEntity = new Product(productId, PRODUCT_NAME, PRODUCT_DESCRIPTION);
    var productResponse = buildProductResponse(productId);

    Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productEntity);
    Mockito.when(productApplicationMapper.toResponse(productEntity)).thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(productEntity);

    Product savedProduct = productCaptor.getValue();
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        null, Stock.of(PRODUCT_STOCK),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 0);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should create product with null stock using Stock.empty()")
  void should_create_product_with_null_stock() {
    var request = new CreateProductRequest(
        PRODUCT_SKU, PRODUCT_NAME, PRODUCT_COST, PRODUCT_PRICE, null,
        PRODUCT_DESCRIPTION, PRODUCT_BARCODE, null, null, null);
    var productId = ProductId.generate();
    var productEntity = new Product(productId, PRODUCT_NAME, PRODUCT_DESCRIPTION);
    var productResponse = buildProductResponse(productId);

    Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productEntity);
    Mockito.when(productApplicationMapper.toResponse(productEntity)).thenReturn(productResponse);

    ProductResponse result = createProductUseCase.execute(request);

    assertNotNull(result);
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(productRepository).save(productCaptor.capture());
    Mockito.verify(productApplicationMapper).toResponse(productEntity);

    Product savedProduct = productCaptor.getValue();
    assertProductVariant(savedProduct, Sku.from(PRODUCT_SKU),
        new Barcode(PRODUCT_BARCODE), Stock.empty(),
        new Money(PRODUCT_COST), new Money(PRODUCT_PRICE), 0);
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should throw DuplicateSkuException when SKU already exists")
  void should_throw_exception_when_sku_already_exists() {
    var request = buildNewProductRequest(ProductOptionalData.builder().build());
    Mockito.when(productRepository.existsBySku(Mockito.any(Sku.class))).thenReturn(true);

    assertThrows(DuplicateSkuException.class, () -> createProductUseCase.execute(request));

    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when brand does not exist")
  void should_throw_exception_when_brand_does_not_exist() {
    var request = buildNewProductRequest(ProductOptionalData.builder().brandId(3).build());
    Mockito.when(brandRepository.findById(Mockito.any(BrandId.class)))
        .thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> createProductUseCase.execute(request));

    assertTrue(ex.getMessage().contains("Brand not found"));
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(brandRepository).findById(Mockito.any(BrandId.class));
    Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when category does not exist")
  void should_throw_exception_when_category_does_not_exist() {
    Set<Integer> categories = Set.of(1);
    var request = buildNewProductRequest(
        ProductOptionalData.builder().categories(categories).build());
    Mockito.when(categoryRepository.findById(Mockito.any(CategoryId.class)))
        .thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> createProductUseCase.execute(request));

    assertTrue(ex.getMessage().contains("Category not found"));
    Mockito.verify(dtoValidator).validate(Mockito.any(CreateProductRequest.class));
    Mockito.verify(productRepository).existsBySku(Mockito.any(Sku.class));
    Mockito.verify(categoryRepository).findById(Mockito.any(CategoryId.class));
    Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    Mockito.verifyNoMoreInteractions(productRepository, brandRepository, categoryRepository,
        propertyRepository, productApplicationMapper, dtoValidator);
  }
}
