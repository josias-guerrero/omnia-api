package org.josiasguerrero.products.infrastructure.persistence.mapper;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.valueobject.Barcode;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.ProductId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.PropertyValue;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.products.domain.valueobject.Stock;
import org.josiasguerrero.products.infrastructure.persistence.entity.ProductJpaEntity;
import org.josiasguerrero.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {
  public Product toDomain(ProductJpaEntity entity) {
    // Convertir ID
    ProductId id = ProductId.from(bytesToUUID(entity.getId()).toString());

    // Convertir value objects
    Sku sku = Sku.from(entity.getSku());
    Money cost = new Money(entity.getCost());
    Money price = new Money(entity.getPrice());
    Stock stock = new Stock(entity.getStock());

    Barcode barcode = entity.getBarcode() != null
        ? new Barcode(entity.getBarcode())
        : null;

    BrandId brandId = entity.getBrandId() != null
        ? BrandId.from(entity.getBrandId().toString())
        : null;

    // Convertir categorías (solo IDs)
    Set<CategoryId> categoryIds = entity.getCategories().stream()
        .map(cat -> CategoryId.from(cat.getId()))
        .collect(Collectors.toSet());

    // Convertir propiedades
    Map<PropertyId, PropertyValue> properties = entity.getProperties().stream()
        .collect(Collectors.toMap(
            pp -> PropertyId.from(pp.getProperty().getId()),
            pp -> PropertyValue.of(pp.getValue())));

    return new Product(
        id, sku, entity.getName(), entity.getDescription(), barcode,
        cost, price, stock, brandId, categoryIds, properties,
        entity.getCreatedAt(), entity.getUpdatedAt());
  }

  public ProductJpaEntity toJpaEntity(Product domain) {
    return ProductJpaEntity.builder()
        .id(uuidToBytes(domain.getId().value()))
        .sku(domain.getSku().value())
        .name(domain.getName())
        .description(domain.getDescription())
        .barcode(domain.getBarcode() != null ? domain.getBarcode().value() : null)
        .cost(domain.getCost().amount())
        .price(domain.getPrice().amount())
        .stock(domain.getStock().quantity())
        .brandId(domain.getBrandId() != null ? domain.getBrandId().value() : null)
        .createdAt(domain.getCreatedAt())
        .updatedAt(domain.getUpdatedAt())
        .build();
  }

  public byte[] uuidToBytes(UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }

  public UUID bytesToUUID(byte[] bytes) {

    ByteBuffer bb = ByteBuffer.wrap(bytes);
    long high = bb.getLong();
    long low = bb.getLong();
    return new UUID(high, low);
  }

}
