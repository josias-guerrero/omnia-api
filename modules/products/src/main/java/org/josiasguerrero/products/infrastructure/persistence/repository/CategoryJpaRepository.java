package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.Optional;

import org.josiasguerrero.products.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Integer> {
  Optional<CategoryJpaEntity> findByName(String name);

  boolean existsByName(String name);

  @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
      "FROM ProductJpaEntity p JOIN p.categories c WHERE c.id = :categoryId")
  boolean hasProducts(@Param("categoryId") Integer categoryId);
}
