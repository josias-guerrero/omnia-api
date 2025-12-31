package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.josiasguerrero.products.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {

  Optional<ProductJpaEntity> findBySku(String sku);

  boolean existsBySku(String sku);

  @Query("Select p FROM ProductJpaEntity p JOIN p.categories c WHERE c.id = :categoryId")
  List<ProductJpaEntity> findByCategory(@Param("categoryId") Integer categoryId);

  List<ProductJpaEntity> findByBrandId(Integer brandId);

  List<ProductJpaEntity> findByStockLessThan(Integer threshold);

  @Query("Select p FROM ProductJpaEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
  List<ProductJpaEntity> findByNameContaining(@Param("name") String name);
}
