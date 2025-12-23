package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.Optional;

import org.josiasguerrero.products.infrastructure.persistence.entity.BrandJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandJpaRepository extends JpaRepository<BrandJpaEntity, Integer> {

  Optional<BrandJpaEntity> findByName(String name);

  boolean existsByName(String name);

  @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
      "FROM ProductJpaEntity p WHERE p.brandId = :brandId")
  boolean hasProducts(@Param("brandId") Integer brandId);
}
