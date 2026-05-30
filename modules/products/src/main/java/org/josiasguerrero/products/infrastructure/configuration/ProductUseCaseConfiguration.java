package org.josiasguerrero.products.infrastructure.configuration;

import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.application.usecase.Product.CreateProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.CreateProductVariantUseCase;
import org.josiasguerrero.products.application.usecase.Product.DeleteProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.FindProductByIdUseCase;
import org.josiasguerrero.products.application.usecase.Product.SearchProductsQueryHandler;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductCategoriesUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductUseCase;
import org.josiasguerrero.products.application.usecase.Product.UpdateProductVariantUseCase;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.port.PropertyDomainService;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.port.SkuGeneratorPort;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductUseCaseConfiguration {

  @Bean
  public CreateProductUseCase createProductUseCase(
      SkuGeneratorPort skuGenerator,
      PropertyDomainService propertyDomainService,
      ProductRepository productRepository,
      BrandRepository brandRepository,
      CategoryRepository categoryRepository,
      PropertyRepository propertyRepository,
      DtoValidator dtoValidator,
      ProductApplicationMapper productApplicationMapper) {
    return new CreateProductUseCase(
        propertyDomainService,
        productRepository,
        brandRepository,
        categoryRepository,
        dtoValidator,
        productApplicationMapper,
        skuGenerator);
  }

  @Bean
  public ProductApplicationMapper productApplicationMapper(
      BrandRepository brandRepository,
      CategoryRepository categoryRepository,
      PropertyRepository propertyRepository) {
    return new ProductApplicationMapper(brandRepository, categoryRepository, propertyRepository);
  }

  @Bean
  public FindProductByIdUseCase findProductByIdUseCase(
      ProductRepository productRepository, ProductApplicationMapper productApplicationMapper) {
    return new FindProductByIdUseCase(productRepository, productApplicationMapper);
  }

  @Bean
  public SearchProductsQueryHandler searchProductsQueryHandler(
      ProductRepository productRepository, ProductApplicationMapper mapper) {
    return new SearchProductsQueryHandler(productRepository, mapper);
  }

  @Bean
  public UpdateProductUseCase updateProductUseCase(
      ProductRepository productRepository,
      BrandRepository brandRepository,
      DtoValidator dtoValidator,
      ProductApplicationMapper productApplicationMapper,
      CategoryRepository categoryRepository) {
    return new UpdateProductUseCase(
        productRepository,
        brandRepository,
        categoryRepository,
        dtoValidator,
        productApplicationMapper);
  }

  @Bean
  public DeleteProductUseCase deleteProductUseCase(ProductRepository productRepository) {
    return new DeleteProductUseCase(productRepository);
  }

  @Bean
  public UpdateProductCategoriesUseCase updateProductCategoriesUseCase(
      ProductRepository productRepository,
      CategoryRepository categoryRepository,
      ProductApplicationMapper productApplicationMapper) {
    return new UpdateProductCategoriesUseCase(
        productRepository, categoryRepository, productApplicationMapper);
  }

  @Bean
  public CreateProductVariantUseCase createProductVariantUseCase(
      BrandRepository brandRepository,
      SkuGeneratorPort skuGenerator,
      ProductRepository productRepository,
      PropertyDomainService propertyDomainService,
      DtoValidator dtoValidator,
      ProductApplicationMapper productApplicationMapper) {
    return new CreateProductVariantUseCase(
        propertyDomainService,
        productRepository,
        dtoValidator,
        productApplicationMapper,
        skuGenerator,
        brandRepository);
  }

  @Bean
  public UpdateProductVariantUseCase updateProductVariantUseCase(
      ProductRepository productRepository,
      PropertyDomainService propertyDomainService,
      DtoValidator dtoValidator,
      ProductApplicationMapper productApplicationMapper) {
    return new UpdateProductVariantUseCase(
        productRepository, dtoValidator, propertyDomainService, productApplicationMapper);
  }
}
