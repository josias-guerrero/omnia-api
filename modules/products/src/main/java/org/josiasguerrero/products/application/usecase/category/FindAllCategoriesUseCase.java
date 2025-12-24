package org.josiasguerrero.products.application.usecase.category;

import java.util.List;

import org.josiasguerrero.products.application.dto.response.CategoryResponse;
import org.josiasguerrero.products.application.mapper.CategoryMapper;
import org.josiasguerrero.products.domain.port.CategoryRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindAllCategoriesUseCase {
  private CategoryRepository categoryRepository;

  public List<CategoryResponse> execute() {
    return categoryRepository.findAll().stream().map(CategoryMapper::toResponse).toList();
  }

}
