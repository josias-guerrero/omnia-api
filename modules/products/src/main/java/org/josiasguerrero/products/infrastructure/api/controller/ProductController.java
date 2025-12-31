package org.josiasguerrero.products.infrastructure.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.josiasguerrero.products.application.dto.request.CreateProductRequest;
import org.josiasguerrero.products.application.dto.request.UpdateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.usecase.Product.CreateProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.DeleteProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.FindAllProductsUseCase;
import org.josiasguerrero.products.application.usecase.Product.FindProductByIdUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductCategoriesUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductPropertiesUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
  private final CreateProductUseCase createProductUseCase;
  private final DeleteProductUseCase deleteProductUseCase;
  private final FindProductByIdUseCase findProductByIdUseCase;
  private final UpdateProductUseCase updateProductUseCase;
  private final UpdateProductCategoriesUseCase updateProductCategoriesUseCase;
  private final UpdateProductPropertiesUseCase updateProductPropertiesUseCase;
  private final FindAllProductsUseCase findAllProductsUseCase;

  @PostMapping
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
    // TODO: Crear servicio de generador de sku
    ProductResponse response = createProductUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ProductResponse> delete(@PathVariable String id) {
    deleteProductUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> findById(@PathVariable String id) {
    ProductResponse response = findProductByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> findAll() {
    List<ProductResponse> response = findAllProductsUseCase.execute();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> update(@PathVariable String id,
      @Valid @RequestBody UpdateProductRequest request) {
    ProductResponse response = updateProductUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/categories")
  public ResponseEntity<ProductResponse> updateCategories(@PathVariable String id,
      @RequestBody Set<Integer> categoryIds) {

    ProductResponse response = updateProductCategoriesUseCase.exceute(id, categoryIds);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/properties")
  public ResponseEntity<ProductResponse> updateProperties(@PathVariable String id,
      @RequestBody Map<String, String> properties) {
    ProductResponse response = updateProductPropertiesUseCase.exceute(id, properties);

    return ResponseEntity.ok(response);
  }

  // CONSIDER: Add individual remove and add category for individual update
}
