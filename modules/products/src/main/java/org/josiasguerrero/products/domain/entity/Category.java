package org.josiasguerrero.products.domain.entity;

import java.time.LocalDateTime;

import org.josiasguerrero.products.domain.valueobject.CategoryId;

public class Category {
  private CategoryId id;
  private String name;
  private String description;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Category(String name, String description) {
    this.name = name;
    this.description = description;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public Category(CategoryId id, String name, String description) {
    this.id = id;
    this.name = validateName(name);
    this.description = validateDescription(description);
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public Category(
      CategoryId id,
      String name,
      String description,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.name = validateName(name);
    this.description = validateDescription(description);
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public void updateName(String newName) {
    this.name = validateName(newName);
    this.updatedAt = LocalDateTime.now();
  }

  public void updateDescription(String newDescription) {
    this.description = validateDescription(newDescription);
    this.updatedAt = LocalDateTime.now();
  }

  private String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name cannot be null or empty");
    }
    String trimmed = name.trim();
    if (trimmed.length() > 50) {
      throw new IllegalArgumentException("Category name cannot exceed 50 characters");
    }
    return trimmed;
  }

  private String validateDescription(String description) {
    if (description != null && description.length() > 100) {
      throw new IllegalArgumentException("Category description cannot exceed 100 characters");
    }
    return description;
  }

  public CategoryId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

}
