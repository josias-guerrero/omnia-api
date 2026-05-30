package org.josiasguerrero.products.application.usecase.Product;

import java.util.Set;

import org.josiasguerrero.products.application.dto.request.UpdateProductRequest;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.exception.CategoryNotFoundException;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateProductUseCase {

  private final ProductRepository productRepository;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final DtoValidator dtoValidator;
  private final ProductApplicationMapper productApplicationMapper;

  public ProductResponse execute(String productId, UpdateProductRequest request) {
    dtoValidator.validate(request);

    ProductId id = ProductId.from(productId);
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    validateBusinessRules(request);
    updateProductFields(product, request);
    updateCategories(product, request.categoryIds());

    productRepository.save(product);

    return productApplicationMapper.toResponse(product);
  }

  private void updateCategories(Product product, Set<Integer> categoryIds) {
    if (categoryIds == null)
      return;

    validateAllCategoriesExist(categoryIds);

    product.clearCategories();
    categoryIds.forEach(catId -> product.assignToCategory(CategoryId.from(catId)));
  }

  private void validateAllCategoriesExist(Set<Integer> categoryIds) {
    for (Integer catId : categoryIds) {
      CategoryId categoryId = CategoryId.from(catId);
      categoryRepository.findById(categoryId)
          .orElseThrow(() -> new CategoryNotFoundException(
              "Category not found: " + catId));
    }
  }

  private void validateBusinessRules(UpdateProductRequest request) {
    if (request.brandId() != null && !request.brandId().isBlank()) {
      BrandId brandId = BrandId.from(request.brandId());
      brandRepository.findById(brandId)
          .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));
    }
  }

  private void updateProductFields(Product product, UpdateProductRequest request) {
    if (request.name() != null && !request.name().isBlank()) {
      product.rename(request.name());
    }

    if (request.description() != null) {
      product.changeDescription(request.description());
    }

    if (request.brandId() != null && !request.brandId().isBlank()) {
      product.assignToBrand(BrandId.from(request.brandId()));
    }
  }
}
