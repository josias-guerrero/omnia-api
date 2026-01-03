package org.josiasguerrero.products.domain.port;

import java.util.Optional;

import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.shared.domain.pagination.Page;
import org.josiasguerrero.shared.domain.pagination.PageRequest;

public interface ProductRepository {
  void save(Product product);

  Optional<Product> findById(ProductId id);

  Optional<Product> findBySku(Sku sku);

  Page<Product> findAll(PageRequest pageRequest);

  Page<Product> findByCategory(CategoryId categoryId, PageRequest pageRequest);

  Page<Product> findByBrand(BrandId brandId, PageRequest pageRequest);

  boolean existsBySku(Sku sku);

  void delete(ProductId id);

  // Queries útiles para el negocio
  Page<Product> findLowStock(int threshold, PageRequest pageRequest);

  Page<Product> findByName(String name, PageRequest pageRequest);
}
