package org.josiasguerrero.products.infrastructure.configuration;

import org.josiasguerrero.products.application.usecase.Product.CreateProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.DeleteProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.FindProductByIdUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductCategoriesUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductPropertiesUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductUseCase;
import org.josiasguerrero.products.application.usecase.brand.CreateBrandUseCase;
import org.josiasguerrero.products.application.usecase.brand.DeleteBrandUseCase;
import org.josiasguerrero.products.application.usecase.brand.FindBrandByIdUseCase;
import org.josiasguerrero.products.application.usecase.brand.FindBrandByNameUseCase;
import org.josiasguerrero.products.application.usecase.brand.UpdateBrandUseCase;
import org.josiasguerrero.products.application.usecase.category.CreateCategoryUseCase;
import org.josiasguerrero.products.application.usecase.category.DeleteCategoryUseCase;
import org.josiasguerrero.products.application.usecase.category.FindCategoryByIdUseCase;
import org.josiasguerrero.products.application.usecase.category.UpdateCategoryUseCase;
import org.josiasguerrero.products.application.usecase.property.CreatePropertyUseCase;
import org.josiasguerrero.products.application.usecase.property.DeletePropertyUseCase;
import org.josiasguerrero.products.application.usecase.property.FindPropertyByIdUseCase;
import org.josiasguerrero.products.application.usecase.property.UpdatePropertyUseCase;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfigurartion {
  // TODO: FindAllUsecases for Brand, Category and Property

  // ========== PRODUCT USE CASES ==========
  @Bean
  public CreateProductUseCase createProductUseCase(
      ProductRepository productRepository,
      BrandRepository brandRepository,
      CategoryRepository categoryRepository,
      PropertyRepository propertyRepository,
      DtoValidator dtoValidator) {
    return new CreateProductUseCase(productRepository, brandRepository, categoryRepository, propertyRepository,
        dtoValidator);
  }

  @Bean
  public FindProductByIdUseCase findProductByIdUseCase(
      ProductRepository productRepository) {
    return new FindProductByIdUseCase(productRepository);
  }

  @Bean
  public UpdateProductUseCase updateProductUseCase(
      ProductRepository productRepository,
      BrandRepository brandRepository,
      DtoValidator dtoValidator) {
    return new UpdateProductUseCase(productRepository, brandRepository, dtoValidator);
  }

  @Bean
  public DeleteProductUseCase deleteProductUseCase(
      ProductRepository productRepository) {
    return new DeleteProductUseCase(productRepository);
  }

  @Bean
  public UpdateProductCategoriesUseCase updateProductCategoriesUseCase(
      ProductRepository productRepository,
      CategoryRepository categoryRepository) {
    return new UpdateProductCategoriesUseCase(
        productRepository,
        categoryRepository);
  }

  @Bean
  public UpdateProductPropertiesUseCase updateProductPropertiesUseCase(
      ProductRepository productRepository,
      PropertyRepository propertyRepository) {
    return new UpdateProductPropertiesUseCase(
        productRepository,
        propertyRepository);
  }

  // ========== CATEGORY USE CASES ==========

  @Bean
  public CreateCategoryUseCase createCategoryUseCase(
      CategoryRepository categoryRepository,
      DtoValidator validator) {
    return new CreateCategoryUseCase(categoryRepository, validator);
  }

  @Bean
  public FindCategoryByIdUseCase findCategoryByIdUseCase(
      CategoryRepository categoryRepository) {
    return new FindCategoryByIdUseCase(categoryRepository);
  }

  @Bean
  public UpdateCategoryUseCase updateCategoryUseCase(
      CategoryRepository categoryRepository,
      DtoValidator dtoValidator) {
    return new UpdateCategoryUseCase(categoryRepository, dtoValidator);
  }

  @Bean
  public DeleteCategoryUseCase deleteCategoryUseCase(
      CategoryRepository categoryRepository) {
    return new DeleteCategoryUseCase(categoryRepository);
  }

  // ========== BRAND USE CASES ==========

  @Bean
  public CreateBrandUseCase createBrandUseCase(
      BrandRepository brandRepository,
      DtoValidator validator) {
    return new CreateBrandUseCase(brandRepository, validator);
  }

  @Bean
  public FindBrandByIdUseCase findBrandByIdUseCase(
      BrandRepository brandRepository,
      DtoValidator validator) {
    return new FindBrandByIdUseCase(brandRepository);
  }

  @Bean
  FindBrandByNameUseCase findBrandByNameUseCase(
      BrandRepository brandRepository) {
    return new FindBrandByNameUseCase(brandRepository);
  }

  @Bean
  public UpdateBrandUseCase updateBrandUseCase(
      BrandRepository brandRepository,
      DtoValidator validator) {
    return new UpdateBrandUseCase(brandRepository, validator);
  }

  @Bean
  public DeleteBrandUseCase deleteBrandUseCase(
      BrandRepository brandRepository) {
    return new DeleteBrandUseCase(brandRepository);
  }

  // ========== PROPERTY USE CASES ==========

  @Bean
  public CreatePropertyUseCase createPropertyUseCase(
      PropertyRepository propertyRepository,
      DtoValidator validator) {
    return new CreatePropertyUseCase(propertyRepository, validator);
  }

  @Bean
  public FindPropertyByIdUseCase findPropertyByIdUseCase(
      PropertyRepository propertyRepository) {
    return new FindPropertyByIdUseCase(propertyRepository);
  }

  @Bean
  public UpdatePropertyUseCase updatePropertyUseCase(
      PropertyRepository propertyRepository,
      DtoValidator validator) {
    return new UpdatePropertyUseCase(propertyRepository, validator);
  }

  @Bean
  public DeletePropertyUseCase deletePropertyUseCase(
      PropertyRepository propertyRepository) {
    return new DeletePropertyUseCase(propertyRepository);
  }

}
