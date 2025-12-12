package org.josiasguerrero.products.domain.port;

import java.util.List;
import java.util.Optional;

import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.Sku;

public interface ProductRepository {
  void save(Product product);

  Optional<Product> findById(ProductId id);

  Optional<Product> findBySku(Sku sku);

  List<Product> findAll();

  List<Product> findByCategory(CategoryId categoryId);

  List<Product> findByBrand(BrandId brandId);

  boolean existsBySku(Sku sku);

  void delete(ProductId id);

  // Queries útiles para el negocio
  List<Product> findLowStock(int threshold);

  List<Product> findByName(String name);
}
