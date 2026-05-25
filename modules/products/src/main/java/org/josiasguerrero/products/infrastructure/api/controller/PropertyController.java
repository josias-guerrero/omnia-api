package org.josiasguerrero.products.infrastructure.api.controller;

import java.util.List;

import org.josiasguerrero.products.application.dto.request.CreatePropertyRequest;
import org.josiasguerrero.products.application.dto.request.UpdatePropertyRequest;
import org.josiasguerrero.products.application.dto.response.PropertyResponse;
import org.josiasguerrero.products.application.usecase.property.CreatePropertyUseCase;
import org.josiasguerrero.products.application.usecase.property.DeletePropertyUseCase;
import org.josiasguerrero.products.application.usecase.property.FindAllPropertiesUseCase;
import org.josiasguerrero.products.application.usecase.property.FindPropertyByIdUseCase;
import org.josiasguerrero.products.application.usecase.property.UpdatePropertyUseCase;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/properties")
@Tag(name = "Properties", description = "Endpoints for managing product custom properties")
public class PropertyController {
  private final CreatePropertyUseCase createPropertyUseCase;
  private final DeletePropertyUseCase deletePropertyUseCase;
  private final FindPropertyByIdUseCase findPropertyByIdUseCase;
  private final UpdatePropertyUseCase updatePropertyUseCase;
  private final FindAllPropertiesUseCase findAllPropertiesUseCase;

  @PostMapping
  @Operation(summary = "Create a property", description = "Creates a new custom property definition")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Property created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  public ResponseEntity<PropertyResponse> create(@Valid @RequestBody CreatePropertyRequest request) {
    var response = createPropertyUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a property", description = "Deletes a property definition by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Property deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Property not found")
  })
  public ResponseEntity<Void> delete(@Parameter(description = "Property ID") @PathVariable("id") Integer id) {
    deletePropertyUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "List all properties", description = "Retrieves all custom property definitions")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "List of properties")
  })
  public ResponseEntity<List<PropertyResponse>> findAll() {
    var response = findAllPropertiesUseCase.execute();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Find property by ID", description = "Retrieves a property definition by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Property found"),
      @ApiResponse(responseCode = "404", description = "Property not found")
  })
  public ResponseEntity<PropertyResponse> findById(@Parameter(description = "Property ID") @PathVariable("id") Integer id) {
    var response = findPropertyByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a property", description = "Updates an existing property definition by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Property updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Property not found")
  })
  public ResponseEntity<PropertyResponse> update(@Parameter(description = "Property ID") @PathVariable("id") Integer id, UpdatePropertyRequest request) {
    var response = updatePropertyUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }
}
