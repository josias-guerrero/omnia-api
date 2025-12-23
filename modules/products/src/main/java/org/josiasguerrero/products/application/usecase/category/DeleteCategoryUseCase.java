package org.josiasguerrero.products.application.usecase.category;

import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.valueobject.CategoryId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteCategoryUseCase {

  private CategoryRepository categoryRepository;

  public void execute(Integer catId) {
    CategoryId id = CategoryId.from(catId);

    if (categoryRepository.hasProducts(id)) {
      throw new IllegalStateException("Cannot delete category with associated products. "
          + "remove products from this category first.");
    }

    categoryRepository.delete(id);
  }
}
