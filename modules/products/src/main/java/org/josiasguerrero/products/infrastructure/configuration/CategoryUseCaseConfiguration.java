package org.josiasguerrero.products.infrastructure.configuration;

import org.josiasguerrero.products.application.usecase.category.CreateCategoryUseCase;
import org.josiasguerrero.products.application.usecase.category.DeleteCategoryUseCase;
import org.josiasguerrero.products.application.usecase.category.FindAllCategoriesUseCase;
import org.josiasguerrero.products.application.usecase.category.FindCategoryByIdUseCase;
import org.josiasguerrero.products.application.usecase.category.UpdateCategoryUseCase;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfiguration {

  @Bean
  public CreateCategoryUseCase createCategoryUseCase(
      CategoryRepository categoryRepository, DtoValidator validator) {
    return new CreateCategoryUseCase(categoryRepository, validator);
  }

  @Bean
  public FindAllCategoriesUseCase findAllCategoriesUseCase(CategoryRepository categoryRepository) {
    return new FindAllCategoriesUseCase(categoryRepository);
  }

  @Bean
  public FindCategoryByIdUseCase findCategoryByIdUseCase(CategoryRepository categoryRepository) {
    return new FindCategoryByIdUseCase(categoryRepository);
  }

  @Bean
  public UpdateCategoryUseCase updateCategoryUseCase(
      CategoryRepository categoryRepository, DtoValidator dtoValidator) {
    return new UpdateCategoryUseCase(categoryRepository, dtoValidator);
  }

  @Bean
  public DeleteCategoryUseCase deleteCategoryUseCase(CategoryRepository categoryRepository) {
    return new DeleteCategoryUseCase(categoryRepository);
  }
}
