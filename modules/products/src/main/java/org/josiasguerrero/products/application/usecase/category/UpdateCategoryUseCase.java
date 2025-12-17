package org.josiasguerrero.products.application.usecase.category;

import org.josiasguerrero.products.application.dto.request.UpdateCategoryRequest;
import org.josiasguerrero.products.domain.entity.Category;
import org.josiasguerrero.products.domain.exception.CategoryNotFoundException;
import org.josiasguerrero.products.domain.exception.DuplicateCategoryNameException;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateCategoryUseCase {

  private CategoryRepository categoryRepository;
  private DtoValidator dtoValidator;

  public void execute(UpdateCategoryRequest request) {
    dtoValidator.validate(request);

    if (categoryRepository.existsByName(request.name())) {
      throw new DuplicateCategoryNameException(request.name());
    }

    CategoryId categoryId = CategoryId.from(request.id());
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    if (request.name() != null) {
      category.updateName(request.name());
    }

    if (request.description() != null) {
      category.updateDescription(request.description());
    }

  }

}
