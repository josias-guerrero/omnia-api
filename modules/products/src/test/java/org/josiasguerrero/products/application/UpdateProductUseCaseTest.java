package org.josiasguerrero.products.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.josiasguerrero.products.application.dto.request.UpdateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductUseCase;
import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.entity.Category;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductUseCase")
class UpdateProductUseCaseTest {

  @Mock
  private ProductRepository productRepository;
  @Mock
  private BrandRepository brandRepository;
  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private DtoValidator dtoValidator;
  @Mock
  private ProductApplicationMapper productApplicationMapper;

  @InjectMocks
  private UpdateProductUseCase updateProductUseCase;

  @Test
  @DisplayName("Should update product name and description successfully")
  void should_update_product_successfully() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Old Name", "Old Description");
    UpdateProductRequest request = new UpdateProductRequest("New Name", "New Description", null, null);

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productApplicationMapper.toResponse(product)).thenReturn(
        new ProductResponse(productId.value().toString(), "New Name", "New Description", null, Set.of(), Set.of(), LocalDateTime.now(), LocalDateTime.now())
    );

    ProductResponse response = updateProductUseCase.execute(productId.value().toString(), request);

    assertNotNull(response);
    assertEquals("New Name", response.name());
    assertEquals("New Description", response.description());
    assertEquals("New Name", product.getName());
    assertEquals("New Description", product.getDescription());

    verify(dtoValidator).validate(request);
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("Should assign product to new brand")
  void should_assign_product_to_new_brand() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Name", "Description");
    UpdateProductRequest request = new UpdateProductRequest(null, null, "1", null);
    Brand brand = new Brand(BrandId.from(1), "Brand Name");

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(brandRepository.findById(BrandId.from(1))).thenReturn(Optional.of(brand));

    updateProductUseCase.execute(productId.value().toString(), request);

    assertEquals(BrandId.from(1), product.getBrandId());
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("Should assign product to categories")
  void should_assign_product_to_categories() {
    ProductId productId = ProductId.generate();
    Product product = new Product(productId, "Name", "Description");
    UpdateProductRequest request = new UpdateProductRequest(null, null, null, Set.of(2));
    Category category = new Category(CategoryId.from(2), "Category Name", null);

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(categoryRepository.findById(CategoryId.from(2))).thenReturn(Optional.of(category));

    updateProductUseCase.execute(productId.value().toString(), request);

    assertTrue(product.getCategoryIds().contains(CategoryId.from(2)));
    verify(productRepository).save(product);
  }
}
