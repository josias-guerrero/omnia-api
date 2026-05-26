package org.josiasguerrero.products.domain.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;

public class Product {

  private final ProductId id;

  private String name;
  private String description;

  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private BrandId brandId;

  private Set<ProductVariant> variants;

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setBrandId(BrandId brandId) {
    this.brandId = brandId;
  }

  public Set<ProductVariant> getVariants() {
    return variants;
  }

  public void setVariants(Set<ProductVariant> variants) {
    this.variants = variants;
  }

  public void setCategoryIds(Set<CategoryId> categoryIds) {
    this.categoryIds = categoryIds;
  }

  private Set<CategoryId> categoryIds;

  public Product(
      ProductId id,
      String name,
      String description) {
    this.id = id;
    this.name = validateName(name);
    this.description = description;

    this.categoryIds = new HashSet<>();
    this.variants = new HashSet<>();

    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public Product(ProductId id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt,
      BrandId brandId, Set<ProductVariant> variants, Set<CategoryId> categoryIds) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.brandId = brandId;
    this.variants = variants;
    this.categoryIds = categoryIds;
  }

  public void assignToBrand(BrandId brandId) {
    this.brandId = brandId;
    this.updatedAt = LocalDateTime.now();
  }

  public void assignToCategory(CategoryId categoryId) {
    this.categoryIds.add(categoryId);
    this.updatedAt = LocalDateTime.now();
  }

  public void clearCategories() {
    this.categoryIds.clear();
  }

  public void rename(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
    if (name.length() > 100) {
      throw new IllegalArgumentException("Name cannot exceed 100 characters");
    }
    this.name = name;
    this.updatedAt = LocalDateTime.now();
  }

  public void changeDescription(String description) {
    this.description = description;
    this.updatedAt = LocalDateTime.now();
  }

  private String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Product name cannot be null or empty");
    }
    String trimmed = name.trim();
    if (trimmed.length() > 100) {
      throw new IllegalArgumentException("Product name cannot exceed 100 characters");
    }
    return trimmed;
  }

  public ProductId getId() {
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

  public BrandId getBrandId() {
    return brandId;
  }

  public Set<CategoryId> getCategoryIds() {
    return categoryIds;
  }
}
