package org.josiasguerrero.products.infrastructure.api.controller;

import org.josiasguerrero.products.application.dto.request.BrandRequest;
import org.josiasguerrero.products.application.dto.request.UpdateBrandRequest;
import org.josiasguerrero.products.application.dto.response.BrandResponse;
import org.josiasguerrero.products.application.usecase.brand.CreateBrandUseCase;
import org.josiasguerrero.products.application.usecase.brand.DeleteBrandUseCase;
import org.josiasguerrero.products.application.usecase.brand.FindBrandByIdUseCase;
import org.josiasguerrero.products.application.usecase.brand.FindBrandByNameUseCase;
import org.josiasguerrero.products.application.usecase.brand.UpdateBrandUseCase;
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
@RequestMapping("api/v1/brands")
public class BrandController {
  private final CreateBrandUseCase createBrandUseCase;
  private final DeleteBrandUseCase deleteBrandUseCase;
  private final FindBrandByIdUseCase findBrandByIdUseCase;
  private final FindBrandByNameUseCase findBrandByNameUseCase;
  private final UpdateBrandUseCase updateBrandUseCase;

  @PostMapping
  public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
    BrandResponse response = createBrandUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    deleteBrandUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<BrandResponse> findById(@PathVariable Integer id) {
    BrandResponse response = findBrandByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{name}")
  public ResponseEntity<BrandResponse> findByName(@PathVariable String name) {
    BrandResponse response = findBrandByNameUseCase.execute(name);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BrandResponse> update(@PathVariable Integer id,
      @Valid @RequestBody UpdateBrandRequest request) {
    BrandResponse response = updateBrandUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }
}
