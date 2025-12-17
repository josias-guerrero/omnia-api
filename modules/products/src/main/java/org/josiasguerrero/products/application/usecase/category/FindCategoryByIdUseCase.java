package org.josiasguerrero.products.application.usecase.category;

import org.josiasguerrero.products.application.dto.response.CategoryResponse;
import org.josiasguerrero.products.application.mapper.CategoryMapper;
import org.josiasguerrero.products.domain.entity.Category;
import org.josiasguerrero.products.domain.exception.CategoryNotFoundException;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.valueobject.CategoryId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindCategoryByIdUseCase {

  private CategoryRepository categoryRepository;

  public CategoryResponse execute(CategoryId id) {
    if (id == null) {
      throw new IllegalArgumentException("Category Id must not be null");
    }

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryNotFoundException(id));

    return CategoryMapper.toResponse(category);
  }

}
