package org.josiasguerrero.products.infrastructure.api.controller;

import java.util.List;

import org.josiasguerrero.products.application.dto.request.BrandRequest;
import org.josiasguerrero.products.application.dto.request.UpdateBrandRequest;
import org.josiasguerrero.products.application.dto.response.BrandResponse;
import org.josiasguerrero.products.application.usecase.brand.CreateBrandUseCase;
import org.josiasguerrero.products.application.usecase.brand.DeleteBrandUseCase;
import org.josiasguerrero.products.application.usecase.brand.FindAllBrandsUseCase;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/brands")
@Tag(name = "Brands", description = "Endpoints for managing product brands")
public class BrandController {
  private final CreateBrandUseCase createBrandUseCase;
  private final DeleteBrandUseCase deleteBrandUseCase;
  private final FindBrandByIdUseCase findBrandByIdUseCase;
  private final FindBrandByNameUseCase findBrandByNameUseCase;
  private final UpdateBrandUseCase updateBrandUseCase;
  private final FindAllBrandsUseCase findAllBrandsUseCase;

  @PostMapping
  @Operation(summary = "Create a brand", description = "Creates a new product brand")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Brand created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
    BrandResponse response = createBrandUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a brand", description = "Deletes a brand by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Brand not found")
  })
  public ResponseEntity<Void> delete(@Parameter(description = "Brand ID") @PathVariable("id") Integer id) {
    deleteBrandUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "List all brands", description = "Retrieves all product brands")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "List of brands")
  })
  public ResponseEntity<List<BrandResponse>> findAll() {
    List<BrandResponse> response = findAllBrandsUseCase.execute();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Find brand by ID", description = "Retrieves a brand by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Brand found"),
      @ApiResponse(responseCode = "404", description = "Brand not found")
  })
  public ResponseEntity<BrandResponse> findById(@Parameter(description = "Brand ID") @PathVariable("id") Integer id) {
    BrandResponse response = findBrandByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/name/{name}")
  @Operation(summary = "Find brand by name", description = "Retrieves a brand by its name")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Brand found"),
      @ApiResponse(responseCode = "404", description = "Brand not found")
  })
  public ResponseEntity<BrandResponse> findByName(@Parameter(description = "Brand name") @PathVariable("name") String name) {
    BrandResponse response = findBrandByNameUseCase.execute(name);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a brand", description = "Updates an existing brand by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Brand updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Brand not found")
  })
  public ResponseEntity<BrandResponse> update(@Parameter(description = "Brand ID") @PathVariable("id") Integer id,
      @Valid @RequestBody UpdateBrandRequest request) {
    BrandResponse response = updateBrandUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }
}
