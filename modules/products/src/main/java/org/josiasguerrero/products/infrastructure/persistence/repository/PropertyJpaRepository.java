package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.Optional;

import org.josiasguerrero.products.infrastructure.persistence.entity.PropertyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyJpaRepository extends JpaRepository<PropertyJpaEntity, Integer> {
  Optional<PropertyJpaEntity> findByName(String name);

  boolean existsByName(String name);

  @Query("SELECT CASE WHEN COUNT(pp) > 0 THEN true ELSE false END " +
      "FROM ProductPropertyJpaEntity pp WHERE pp.property.id = :propertyId")
  boolean isUsedByProducts(@Param("propertyId") Integer propertyId);

}
