package org.josiasguerrero.products.infrastructure.persistence.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
import org.josiasguerrero.shared.domain.criteria.Criteria;
import org.josiasguerrero.shared.domain.criteria.Filter;
import org.josiasguerrero.shared.domain.pagination.Page;
import org.josiasguerrero.shared.domain.pagination.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

  private final EntityManager entityManager;
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
    if (product.getProperties() == null) {
      entity.clearProperties();
      return;
    }

    // 1. Preload Property definitions for all properties in the domain object
    List<Integer> propertyIds = product.getProperties() == null ? List.of()
        : product.getProperties().keySet().stream()
            .filter(Objects::nonNull)
            .map(PropertyId::value)
            .filter(Objects::nonNull)
            .toList();

    Map<Integer, PropertyJpaEntity> propertiesDefinitionMap = propertyRepository
        .findAllById(propertyIds).stream().collect(Collectors.toMap(PropertyJpaEntity::getId, p -> p));

    // 2. Identify properties to REMOVE (present in JPA but not in Domain)
    Set<Integer> domainPropertyIds = new HashSet<>(propertyIds);
    Set<ProductPropertyJpaEntity> properties = entity.getProperties() != null ? entity.getProperties()
        : new HashSet<>();

    List<ProductPropertyJpaEntity> toRemove = properties.stream()
        .filter(p -> !domainPropertyIds.contains(p.getProperty().getId()))
        .toList();

    toRemove.forEach(entity::removeProperty);

    // 3. Update existing or Add new properties
    product.getProperties().forEach((propId, propValue) -> {
      Integer id = propId.value() != null ? propId.value() : null;
      PropertyJpaEntity definition = propertiesDefinitionMap.get(id);

      if (definition == null) {
        throw new PropertyNotFoundException(propId);
      }

      // Check if it already exists
      Optional<ProductPropertyJpaEntity> existingProp = entity.getProperties().stream()
          .filter(p -> p.getProperty().getId().equals(id))
          .findFirst();

      if (existingProp.isPresent()) {
        // UPDATE
        existingProp.get().setValue(propValue.value());
      } else {
        // ADD
        ProductPropertyJpaEntity newProp = ProductPropertyJpaEntity.builder()
            .property(definition)
            .value(propValue.value())
            .build();
        entity.addProperty(newProp);
      }
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
  public Page<Product> findByCriteria(Criteria criteria) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // Total
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProductJpaEntity> countRoot = countQuery.from(ProductJpaEntity.class);
    countQuery.select(cb.count(countRoot));
    countQuery.where(buildPredicates(criteria.getFilters(), cb, countRoot));

    Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

    // Results

    CriteriaQuery<ProductJpaEntity> query = cb.createQuery(ProductJpaEntity.class);
    Root<ProductJpaEntity> root = query.from(ProductJpaEntity.class);
    query.select(root);
    query.where(buildPredicates(criteria.getFilters(), cb, root));

    if (criteria.getOrder() != null) {
      switch (criteria.getOrder().type()) {
        case ASC -> query.orderBy(cb.asc(root.get(criteria.getOrder().field())));
        case DESC -> query.orderBy(cb.desc(root.get(criteria.getOrder().field())));
      }
    }

    TypedQuery<ProductJpaEntity> typedQuery = entityManager.createQuery(query);
    typedQuery.setFirstResult(criteria.getPageRequest().offset());
    typedQuery.setMaxResults(criteria.getPageRequest().size());

    List<Product> products = typedQuery.getResultList().stream()
        .map(mapper::toDomain)
        .toList();

    return Page.of(products, criteria.getPageRequest(), totalElements);
  }

  private Predicate[] buildPredicates(
      List<Filter> filters,
      CriteriaBuilder cb,
      Root<ProductJpaEntity> root) {
    List<Predicate> predicates = new ArrayList<>();

    for (Filter filter : filters) {
      if (filter.value() == null || filter.value().isBlank()) {
        continue;
      }

      predicates.add(switch (filter.operator()) {
        case EQUAL -> {
          if (filter.field().contains(".")) {
            String[] parts = filter.field().split("\\.");
            yield cb.equal(root.get(parts[0]).get(parts[1]),
                parseValue(filter.value(), parts[1]));
          } else {
            yield cb.equal(root.get(filter.field()), filter.value());
          }
        }

        case NOT_EQUAL -> cb.notEqual(root.get(filter.field()), filter.value());
        case GREATER_THAN -> cb.greaterThan(root.get(filter.field()), filter.value());
        case GREATER_THAN_OR_EQUAL ->
          cb.greaterThanOrEqualTo(root.get(filter.field()), filter.value());
        case LESS_THAN -> cb.lessThan(root.get(filter.field()), filter.value());
        case LESS_THAN_OR_EQUAL ->
          cb.lessThanOrEqualTo(root.get(filter.field()), filter.value());
        case CONTAINS ->
          cb.like(cb.lower(root.get(filter.field())),
              "%" + filter.value().toLowerCase() + "%");
        case NOT_CONTAINS ->
          cb.notLike(cb.lower(root.get(filter.field())),
              "%" + filter.value().toLowerCase() + "%");
        case IN -> root.get(filter.field()).in(List.of(filter.value().split(",")));
        case NOT_IN -> cb.not(root.get(filter.field()).in(List.of(filter.value().split(","))));

        // ManyToManyRelations

        case ANY_IN -> {
          List<Long> categoryIds = filter.getValueAsLongList();
          Join<ProductJpaEntity, CategoryJpaEntity> categoryJoin = root.join(filter.field());
          yield categoryJoin.get("id").in(categoryIds);
        }

        case MEMBER_OF -> {
          Join<ProductJpaEntity, CategoryJpaEntity> categoryJoin = root.join(filter.field());
          yield cb.equal(categoryJoin.get("id"), Long.parseLong(filter.value()));
        }
        case NOT_MEMBER_OF -> {
          Join<ProductJpaEntity, CategoryJpaEntity> categoryJoin = root.join(filter.field());
          yield cb.notEqual(categoryJoin.get("id"), Long.parseLong(filter.value()));
        }
      });
    }
    return predicates.toArray(new Predicate[0]);
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
  public Page<Product> findAll(PageRequest pageRequest) {
    return executePagedQuery(pageRequest, (pageable) -> jpaRepository.findAll(pageable));
  }

  @Override
  public Page<Product> findByCategory(CategoryId categoryId, PageRequest pageRequest) {
    return executePagedQuery(pageRequest, (pageable) -> jpaRepository.findByCategory(categoryId.value(), pageable));
  }

  @Override
  public Page<Product> findByBrand(BrandId brandId, PageRequest pageRequest) {
    return executePagedQuery(pageRequest, (pageable) -> jpaRepository.findByBrandId(brandId.value(), pageable));
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
  public Page<Product> findLowStock(int threshold, PageRequest pageRequest) {
    return executePagedQuery(pageRequest, (pageable) -> jpaRepository.findByStockLessThan(threshold, pageable));
  }

  @Override
  public Page<Product> findByName(String name, PageRequest pageRequest) {
    return executePagedQuery(pageRequest, (pageable) -> jpaRepository.findByNameContaining(name, pageable));
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

  /**
   * Exceutes the query given handling the page entities of spring and returning
   * domain entities.
   * 
   * @param pageRequest   domain entity
   * @param queryFunciton function given that returns a spring Page that must be
   *                      converted to domain entity
   * @return Page<Product>
   */
  private Page<Product> executePagedQuery(
      PageRequest pageRequest,
      Function<Pageable, org.springframework.data.domain.Page<ProductJpaEntity>> queryFunciton) {

    // Spring PageRequest entity
    var springPageRequest = org.springframework.data.domain.PageRequest
        .of(pageRequest.page(), pageRequest.size());

    // Spring Page<T> entity
    var springPage = queryFunciton.apply(springPageRequest);

    List<Product> products = springPage.map(mapper::toDomain).getContent();

    return Page.of(products, pageRequest, springPage.getTotalElements());
  }

  /**
   * Parse values on type
   * 
   * @param value
   * @param fieldName
   * @return
   */
  private Object parseValue(String value, String fieldName) {
    if (fieldName.equalsIgnoreCase("id")) {
      return Long.parseLong(value);
    }
    return value;
  }

}
