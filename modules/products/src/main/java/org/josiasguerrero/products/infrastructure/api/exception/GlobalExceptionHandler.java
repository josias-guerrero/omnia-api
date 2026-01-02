package org.josiasguerrero.products.infrastructure.api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.josiasguerrero.products.domain.exception.BrandNotFoundException;
import org.josiasguerrero.products.domain.exception.CategoryNotFoundException;
import org.josiasguerrero.products.domain.exception.DuplicateBrandException;
import org.josiasguerrero.products.domain.exception.DuplicateCategoryNameException;
import org.josiasguerrero.products.domain.exception.DuplicatePropertyNameException;
import org.josiasguerrero.products.domain.exception.DuplicateSkuException;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.exception.PropertyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  /**
   * Maneja excepciones de validación de Jakarta Validation
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Error")
        .message("Invalid request parameters")
        .details(errors)
        .build();

    return ResponseEntity.badRequest().body(apiError);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiError> handleValidationException(ValidationException ex) {
    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Error")
        .message(ex.getMessage())
        .build();

    return ResponseEntity.badRequest().body(apiError);
  }

  /**
   * Maneja excepciones de entidad no encontrada
   */
  @ExceptionHandler({
      ProductNotFoundException.class,
      CategoryNotFoundException.class,
      BrandNotFoundException.class,
      PropertyNotFoundException.class
  })
  public ResponseEntity<ApiError> handleNotFoundExceptions(RuntimeException ex) {
    log.warn("Resource not found: {}", ex.getMessage());

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Not Found")
        .message(ex.getMessage())
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
  }

  /**
   * Maneja excepciones de duplicados (SKU, nombre único, etc.)
   */
  @ExceptionHandler({
      DuplicateSkuException.class,
      DuplicatePropertyNameException.class,
      DuplicateBrandException.class,
      DuplicateCategoryNameException.class })
  public ResponseEntity<ApiError> handleDuplicateExceptions(RuntimeException ex) {
    log.warn("Duplicate resource: {}", ex.getMessage());

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.CONFLICT.value())
        .error("Conflict")
        .message(ex.getMessage())
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
  }

  /**
   * Maneja excepciones de argumentos inválidos (reglas de negocio)
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    log.warn("Invalid argument: {}", ex.getMessage());

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .build();

    return ResponseEntity.badRequest().body(apiError);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> handleIllegalStateException(
      IllegalStateException ex) {
    log.warn("Invalid state: {}", ex.getMessage());

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .build();

    return ResponseEntity.badRequest().body(apiError);
  }

  /**
   * Maneja cualquier otra excepción no capturada
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("An unexpected error occurred")
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
  }
}
