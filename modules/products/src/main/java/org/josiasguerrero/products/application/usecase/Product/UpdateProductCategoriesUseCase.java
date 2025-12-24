package org.josiasguerrero.products.application.usecase.Product;

import java.util.Set;

import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.exception.CategoryNotFoundException;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateProductCategoriesUseCase {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public ProductResponse exceute(String productId, Set<Integer> categoryIds) {
    ProductId id = ProductId.from(productId);
    Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    validateAllCategoriesExists(categoryIds);

    categoryIds.forEach(catId -> product.assignToCategory(new CategoryId(catId)));

    productRepository.save(product);

    return ProductApplicationMapper.toResponse(product);
  }

  public void validateAllCategoriesExists(Set<Integer> categoryIds) {
    for (Integer catId : categoryIds) {

      CategoryId categoryId = CategoryId.from(catId);
      categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(
          "Category not found: " + catId + ". Create it first at POST /categories"));
    }
  }
}
