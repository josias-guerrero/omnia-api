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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {
  private final CreatePropertyUseCase createPropertyUseCase;
  private final DeletePropertyUseCase deletePropertyUseCase;
  private final FindPropertyByIdUseCase findPropertyByIdUseCase;
  private final UpdatePropertyUseCase updatePropertyUseCase;
  private final FindAllPropertiesUseCase findAllPropertiesUseCase;

  @PostMapping
  public ResponseEntity<PropertyResponse> create(@Valid @RequestBody CreatePropertyRequest request) {
    var response = createPropertyUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    deletePropertyUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<PropertyResponse>> findAll() {
    var response = findAllPropertiesUseCase.execute();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PropertyResponse> findById(@PathVariable Integer id) {
    var response = findPropertyByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PropertyResponse> update(@PathVariable Integer id, UpdatePropertyRequest request) {
    var response = updatePropertyUseCase.exceute(id, request);
    return ResponseEntity.ok(response);
  }
}
