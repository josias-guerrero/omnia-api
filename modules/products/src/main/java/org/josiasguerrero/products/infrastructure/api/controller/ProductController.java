package org.josiasguerrero.products.infrastructure.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.josiasguerrero.products.application.dto.request.CreateProductRequest;
import org.josiasguerrero.products.application.dto.request.UpdateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.query.SearchProductsQuery;
import org.josiasguerrero.products.application.usecase.Product.CreateProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.DeleteProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.FindProductByIdUseCase;
import org.josiasguerrero.products.application.usecase.Product.SearchProductsQueryHandler;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductCategoriesUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductPropertiesUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductUseCase;
import org.josiasguerrero.shared.domain.pagination.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Endpoints for managing products")
public class ProductController {
  private final CreateProductUseCase createProductUseCase;
  private final DeleteProductUseCase deleteProductUseCase;
  private final FindProductByIdUseCase findProductByIdUseCase;
  private final UpdateProductUseCase updateProductUseCase;
  private final UpdateProductCategoriesUseCase updateProductCategoriesUseCase;
  private final UpdateProductPropertiesUseCase updateProductPropertiesUseCase;
  private final SearchProductsQueryHandler searchHandler;

  @PostMapping
  @Operation(summary = "Create a new product", description = "Creates a product with SKU, name, pricing, stock, brand, categories, and properties")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Product created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
    // TODO: Crear servicio de generador de sku
    ProductResponse response = createProductUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a product", description = "Deletes a product by its unique ID")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ResponseEntity<ProductResponse> delete(@Parameter(description = "Product ID") @PathVariable("id") String id) {
    deleteProductUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Find product by ID", description = "Retrieves a product by its unique ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Product found"),
      @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ResponseEntity<ProductResponse> findById(
      @Parameter(description = "Product ID") @PathVariable("id") String id) {
    ProductResponse response = findProductByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "Search products", description = "Searches products with optional filters (brand, categories, low stock, text search) and pagination")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Search results returned successfully")
  })
  public ResponseEntity<Page<ProductResponse>> searchProducts(
      @Parameter(description = "Filter by brand ID") @RequestParam(required = false, name = "brandId") Long brandId,
      @Parameter(description = "Filter by category IDs") @RequestParam(required = false, name = "categoryIds") List<Long> categoryIds,
      @Parameter(description = "Low stock threshold filter") @RequestParam(required = false, name = "lowStockThreshold") Integer lowStockThreshold,
      @Parameter(description = "Text search query") @RequestParam(required = false, name = "search") String search,
      @Parameter(description = "Page number (zero-based)") @RequestParam(name = "page", defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(name = "size", defaultValue = "20") int size) {

    SearchProductsQuery query = new SearchProductsQuery(
        brandId,
        categoryIds,
        lowStockThreshold,
        search,
        page,
        size);
    Page<ProductResponse> response = searchHandler.handle(query);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a product", description = "Updates an existing product by its ID with partial or full data")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Product updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ResponseEntity<ProductResponse> update(@Parameter(description = "Product ID") @PathVariable("id") String id,
      @Valid @RequestBody UpdateProductRequest request) {
    ProductResponse response = updateProductUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/categories")
  @Operation(summary = "Update product categories", description = "Replaces all category associations for a product")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Categories updated successfully"),
      @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ResponseEntity<ProductResponse> updateCategories(
      @Parameter(description = "Product ID") @PathVariable("id") String id,
      @RequestBody Set<Integer> categoryIds) {

    ProductResponse response = updateProductCategoriesUseCase.exceute(id, categoryIds);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/properties")
  @Operation(summary = "Update product properties", description = "Replaces all custom properties (key-value pairs) for a product")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Properties updated successfully"),
      @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ResponseEntity<ProductResponse> updateProperties(
      @Parameter(description = "Product ID") @PathVariable("id") String id,
      @RequestBody Map<String, String> properties) {
    ProductResponse response = updateProductPropertiesUseCase.exceute(id, properties);

    return ResponseEntity.ok(response);
  }

  // CONSIDER: Add individual remove and add category for individual update
}
