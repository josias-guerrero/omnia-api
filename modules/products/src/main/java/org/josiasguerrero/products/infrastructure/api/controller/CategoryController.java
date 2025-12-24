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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
  private final CreateCategoryUseCase createCategoryUseCase;
  private final DeleteCategoryUseCase deleteCategoryUseCase;
  private final FindCategoryByIdUseCase findCategoryByIdUseCase;
  private final UpdateCategoryUseCase updateCategoryUseCase;
  private final FindAllCategoriesUseCase findAllCategoriesUseCase;

  @PostMapping
  public ResponseEntity<CategoryResponse> execute(@RequestBody @Valid CategoryRequest request) {
    CategoryResponse response = createCategoryUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    deleteCategoryUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> findAll() {
    var response = findAllCategoriesUseCase.execute();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> findById(@PathVariable Integer id) {
    CategoryResponse response = findCategoryByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> update(@PathVariable Integer id,
      @Valid @RequestBody UpdateCategoryRequest request) {
    CategoryResponse response = updateCategoryUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }
}
