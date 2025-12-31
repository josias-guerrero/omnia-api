package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.exception.PropertyNotFoundException;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.products.infrastructure.persistence.entity.BrandJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.entity.CategoryJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.entity.ProductJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.entity.ProductPropertyJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.entity.PropertyJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.mapper.ProductPersistenceMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductJpaRepository jpaRepository;
  private final CategoryJpaRepository categoryRepository;
  private final PropertyJpaRepository propertyRepository;
  private final ProductPersistenceMapper mapper;

  @Override
  @Transactional
  public void save(Product product) {

    ProductJpaEntity entity = jpaRepository.findById(product.getId().value()).orElseGet(() -> createNewEntity(product));

    syncBasicFieds(entity, product);

    syncCategories(product, entity);

    syncProperties(product, entity);

    syncBrand(entity, product);

    jpaRepository.save(entity);
  }

  private void syncProperties(Product product, ProductJpaEntity entity) {
    if (product.getProperties().isEmpty()) {
      return;
    }

    if (product.getProperties() == null) {
      return;
    }

    entity.clearProperties();

    List<Integer> propertyIds = product.getProperties().keySet().stream()
        .map(PropertyId::value)
        .toList();

    Map<Integer, PropertyJpaEntity> propertiesMap = propertyRepository
        .findAllById(propertyIds).stream().collect(Collectors.toMap(PropertyJpaEntity::getId, p -> p));

    product.getProperties().forEach((propId, propValue) -> {
      PropertyJpaEntity property = propertiesMap.get(propId.value());

      if (property == null) {
        throw new PropertyNotFoundException(propId);
      }

      ProductPropertyJpaEntity productPropertyJpaEntity = ProductPropertyJpaEntity.builder()
          .property(property)
          .value(propValue.value())
          .build();

      entity.addProperty(productPropertyJpaEntity);
    });
  }

  private void syncCategories(Product product, ProductJpaEntity entity) {
    if (product.getCategoryIds().isEmpty()) {
      entity.getCategories().clear();
      return;
    }

    List<Integer> categoryIds = product.getCategoryIds().stream()
        .map(CategoryId::value)
        .toList();

    Set<CategoryJpaEntity> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));

    if (categories.size() != categoryIds.size()) {
      throw new IllegalStateException("Some categories not found");
    }

    entity.setCategories(categories);
  }

  private void syncBrand(ProductJpaEntity entity, Product product) {
    if (product.getBrandId() == null) {
      entity.setBrandId(null);
      return;
    }

    if (entity.getBrandId() != null &&
        product.getBrandId().value().equals(entity.getBrandId().getId())) {
      return;
    }

    // Stub con solo el ID
    BrandJpaEntity brandStub = BrandJpaEntity.builder()
        .id(product.getBrandId().value())
        .build();

    entity.setBrandId(brandStub);
  }

  @Override
  public Optional<Product> findById(ProductId id) {
    return jpaRepository.findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Product> findBySku(Sku sku) {
    return jpaRepository.findBySku(sku.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<Product> findAll() {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Product> findByCategory(CategoryId categoryId) {
    return jpaRepository.findByCategory(categoryId.value()).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Product> findByBrand(BrandId brandId) {
    return jpaRepository.findByBrandId(brandId.value()).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsBySku(Sku sku) {
    return jpaRepository.existsBySku(sku.value());
  }

  @Override
  public void delete(ProductId id) {
    jpaRepository.deleteById(id.value());
  }

  @Override
  public List<Product> findLowStock(int threshold) {
    return jpaRepository.findByStockLessThan(threshold).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Product> findByName(String name) {
    return jpaRepository.findByNameContaining(name).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  private ProductJpaEntity createNewEntity(Product product) {
    return mapper.toJpaEntity(product);
  }

  private void syncBasicFieds(ProductJpaEntity entity, Product product) {
    entity.setSku(product.getSku().value());
    entity.setName(product.getName());
    entity.setDescription(product.getDescription());
    entity.setBarcode(product.getBarcode() != null ? product.getBarcode().value() : null);
    entity.setCost(product.getCost().amount());
    entity.setPrice(product.getPrice().amount());
    entity.setStock(product.getStock().quantity());
  }

}
