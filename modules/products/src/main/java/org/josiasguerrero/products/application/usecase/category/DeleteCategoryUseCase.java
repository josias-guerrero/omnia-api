package org.josiasguerrero.products.application.usecase.category;

import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.valueobject.CategoryId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteCategoryUseCase {

  private CategoryRepository categoryRepository;

  public void execute(CategoryId id) {
    categoryRepository.delete(id);
  }
}
