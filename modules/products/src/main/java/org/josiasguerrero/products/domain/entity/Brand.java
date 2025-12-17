package org.josiasguerrero.products.domain.entity;

import java.time.LocalDateTime;

import org.josiasguerrero.products.domain.valueobject.BrandId;

public class Brand {
  private BrandId id;
  private String name;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Brand(BrandId id, String name) {
    this.id = id;
    this.name = name;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public Brand(BrandId id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Brand(String name) {
    this.name = name;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public void updateName(String newName) {
    this.name = validateName(newName);
    this.updatedAt = LocalDateTime.now();
  }

  private String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Brand name cannot be null or empty");
    }
    String trimmed = name.trim();
    if (trimmed.length() > 100) {
      throw new IllegalArgumentException("Brand name cannot exceed 100 characters");
    }
    return trimmed;
  }

  public BrandId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

}
