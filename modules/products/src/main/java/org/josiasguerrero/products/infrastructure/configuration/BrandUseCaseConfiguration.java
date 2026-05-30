package org.josiasguerrero.products.infrastructure.configuration;

import org.josiasguerrero.products.application.usecase.brand.CreateBrandUseCase;
import org.josiasguerrero.products.application.usecase.brand.DeleteBrandUseCase;
import org.josiasguerrero.products.application.usecase.brand.FindAllBrandsUseCase;
import org.josiasguerrero.products.application.usecase.brand.FindBrandByIdUseCase;
import org.josiasguerrero.products.application.usecase.brand.FindBrandByNameUseCase;
import org.josiasguerrero.products.application.usecase.brand.UpdateBrandUseCase;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrandUseCaseConfiguration {
  @Bean
  public CreateBrandUseCase createBrandUseCase(
      BrandRepository brandRepository, DtoValidator validator) {
    return new CreateBrandUseCase(brandRepository, validator);
  }

  @Bean
  public FindAllBrandsUseCase findAllBrandsUseCase(BrandRepository brandRepository) {
    return new FindAllBrandsUseCase(brandRepository);
  }

  @Bean
  public FindBrandByIdUseCase findBrandByIdUseCase(
      BrandRepository brandRepository, DtoValidator validator) {
    return new FindBrandByIdUseCase(brandRepository);
  }

  @Bean
  FindBrandByNameUseCase findBrandByNameUseCase(BrandRepository brandRepository) {
    return new FindBrandByNameUseCase(brandRepository);
  }

  @Bean
  public UpdateBrandUseCase updateBrandUseCase(
      BrandRepository brandRepository, DtoValidator validator) {
    return new UpdateBrandUseCase(brandRepository, validator);
  }

  @Bean
  public DeleteBrandUseCase deleteBrandUseCase(BrandRepository brandRepository) {
    return new DeleteBrandUseCase(brandRepository);
  }
}
