package org.josiasguerrero.products.infrastructure.api.controller;

import java.util.List;

import org.josiasguerrero.products.application.dto.request.CategoryRequest;
import org.josiasguerrero.products.application.dto.request.UpdateCategoryRequest;
import org.josiasguerrero.products.application.dto.response.CategoryResponse;
import org.josiasguerrero.products.application.usecase.category.CreateCategoryUseCase;
import org.josiasguerrero.products.application.usecase.category.DeleteCategoryUseCase;
import org.josiasguerrero.products.application.usecase.category.FindAllCategoriesUseCase;
import org.josiasguerrero.products.application.usecase.category.FindCategoryByIdUseCase;
import org.josiasguerrero.products.application.usecase.category.UpdateCategoryUseCase;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints for managing product categories")
public class CategoryController {
  private final CreateCategoryUseCase createCategoryUseCase;
  private final DeleteCategoryUseCase deleteCategoryUseCase;
  private final FindCategoryByIdUseCase findCategoryByIdUseCase;
  private final UpdateCategoryUseCase updateCategoryUseCase;
  private final FindAllCategoriesUseCase findAllCategoriesUseCase;

  @PostMapping
  @Operation(summary = "Create a category", description = "Creates a new product category")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Category created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  public ResponseEntity<CategoryResponse> execute(@RequestBody @Valid CategoryRequest request) {
    CategoryResponse response = createCategoryUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a category", description = "Deletes a category by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Category not found")
  })
  public ResponseEntity<Void> delete(@Parameter(description = "Category ID") @PathVariable("id") Integer id) {
    deleteCategoryUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "List all categories", description = "Retrieves all product categories")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "List of categories")
  })
  public ResponseEntity<List<CategoryResponse>> findAll() {
    var response = findAllCategoriesUseCase.execute();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Find category by ID", description = "Retrieves a category by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Category found"),
      @ApiResponse(responseCode = "404", description = "Category not found")
  })
  public ResponseEntity<CategoryResponse> findById(@Parameter(description = "Category ID") @PathVariable("id") Integer id) {
    CategoryResponse response = findCategoryByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a category", description = "Updates an existing category by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Category updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Category not found")
  })
  public ResponseEntity<CategoryResponse> update(@Parameter(description = "Category ID") @PathVariable("id") Integer id,
      @Valid @RequestBody UpdateCategoryRequest request) {
    CategoryResponse response = updateCategoryUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }
}
