package org.josiasguerrero.products.application.mapper;

import org.josiasguerrero.products.application.dto.response.CategoryResponse;
import org.josiasguerrero.products.domain.entity.Category;

public class CategoryMapper {

  public static CategoryResponse toResponse(Category category) {

    return new CategoryResponse(
        category.getId().value(),
        category.getName(),
        category.getDescription(),
        category.getCreatedAt(),
        category.getUpdatedAt());
  }

}
