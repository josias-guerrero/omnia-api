package org.josiasguerrero.products.application.usecase.category;

import org.josiasguerrero.products.application.dto.request.CategoryRequest;
import org.josiasguerrero.products.application.dto.response.CategoryResponse;
import org.josiasguerrero.products.application.mapper.CategoryMapper;
import org.josiasguerrero.products.domain.entity.Category;
import org.josiasguerrero.products.domain.exception.DuplicateCategoryNameException;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateCategoryUseCase {

  private CategoryRepository categoryRepository;
  private DtoValidator validator;

  public CategoryResponse execute(CategoryRequest request) {
    validator.validate(request);

    if (categoryRepository.existsByName(request.name())) {
      throw new DuplicateCategoryNameException(request.name());
    }

    Category category = new Category(request.name(), request.description());

    categoryRepository.save(category);

    return CategoryMapper.toResponse(category);

  }
}
