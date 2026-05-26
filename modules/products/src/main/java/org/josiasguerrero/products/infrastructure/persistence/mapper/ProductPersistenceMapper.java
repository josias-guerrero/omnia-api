package org.josiasguerrero.products.infrastructure.persistence.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
import org.josiasguerrero.products.domain.valueobject.Barcode;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.ProductVariantId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.PropertyValue;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.products.domain.valueobject.Stock;
import org.josiasguerrero.products.domain.valueobject.VariantAttribute;
import org.josiasguerrero.products.infrastructure.persistence.entity.BrandJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.entity.ProductJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.entity.ProductVariantJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.entity.VariantPropertyJpaEntity;
import org.josiasguerrero.products.infrastructure.persistence.entity.PropertyJpaEntity;
import org.josiasguerrero.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

  public Product toDomain(ProductJpaEntity entity) {
    ProductId id = ProductId.from(entity.getId());

    BrandId brandId = entity.getBrand() != null
        ? BrandId.from(entity.getBrand().getId())
        : null;

    Set<CategoryId> categoryIds = entity.getCategories() != null
        ? entity.getCategories().stream()
            .map(cat -> CategoryId.from(cat.getId()))
            .collect(Collectors.toSet())
        : new HashSet<>();

    Set<ProductVariant> variants = entity.getVariants() != null
        ? entity.getVariants().stream()
            .map(this::toDomainVariant)
            .collect(Collectors.toSet())
        : new HashSet<>();

    return new Product(
        id,
        entity.getName(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        brandId,
        variants,
        categoryIds
    );
  }

  private ProductVariant toDomainVariant(ProductVariantJpaEntity entity) {
    ProductVariantId id = ProductVariantId.from(entity.getId());
    Sku sku = Sku.from(entity.getSku());
    Barcode barcode = entity.getBarcode() != null ? new Barcode(entity.getBarcode()) : null;
    Stock stock = new Stock(entity.getStock());
    Money cost = new Money(entity.getCost());
    Money price = new Money(entity.getPrice());

    Set<VariantAttribute> properties = entity.getProperties() != null
        ? entity.getProperties().stream()
            .map(p -> new VariantAttribute(
                PropertyId.from(p.getProperty().getId()),
                PropertyValue.of(p.getValue())
            ))
            .collect(Collectors.toSet())
        : new HashSet<>();

    return new ProductVariant(
        id,
        sku,
        barcode,
        stock,
        cost,
        price,
        properties,
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public ProductJpaEntity toJpaEntity(Product domain) {
    ProductJpaEntity entity = ProductJpaEntity.builder()
        .id(domain.getId().value())
        .name(domain.getName())
        .description(domain.getDescription())
        .brand(domain.getBrandId() != null
            ? BrandJpaEntity.builder().id(domain.getBrandId().value()).build()
            : null)
        .createdAt(domain.getCreatedAt())
        .updatedAt(domain.getUpdatedAt())
        .build();

    Set<ProductVariantJpaEntity> variants = domain.getVariants() != null
        ? domain.getVariants().stream()
            .map(v -> toJpaVariant(v, entity))
            .collect(Collectors.toSet())
        : new HashSet<>();

    entity.setVariants(variants);

    return entity;
  }

  private ProductVariantJpaEntity toJpaVariant(ProductVariant variant, ProductJpaEntity productEntity) {
    ProductVariantJpaEntity variantEntity = ProductVariantJpaEntity.builder()
        .id(variant.getId().value())
        .sku(variant.getSku().value())
        .barcode(variant.getBarcode() != null ? variant.getBarcode().value() : null)
        .stock(variant.getStock().quantity())
        .cost(variant.getCost().amount())
        .price(variant.getPrice().amount())
        .product(productEntity)
        .createdAt(variant.getCreatedAt())
        .updatedAt(variant.getUpdatedAt())
        .build();

    Set<VariantPropertyJpaEntity> propertyEntities = variant.getProperties() != null
        ? variant.getProperties().stream()
            .map(attr -> VariantPropertyJpaEntity.builder()
                .variant(variantEntity)
                .property(PropertyJpaEntity.builder().id(attr.propertyId().value()).build())
                .value(attr.value().value())
                .build())
            .collect(Collectors.toSet())
        : new HashSet<>();

    variantEntity.setProperties(propertyEntities);

    return variantEntity;
  }

}
