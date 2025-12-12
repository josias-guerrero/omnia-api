package org.josiasguerrero.products.domain.port;

import java.util.List;
import java.util.Optional;

import org.josiasguerrero.products.domain.entity.Category;
import org.josiasguerrero.products.domain.valueobject.CategoryId;

public interface CategoryRepository {

  void save(Category category);

  Optional<Category> findById(CategoryId id);

  Optional<Category> findByName(String name);

  List<Category> findAll();

  boolean existsByName(String name);

  void delete(CategoryId id);

  // Validación de negocio
  boolean hasProducts(CategoryId id);
}
