package org.josiasguerrero.products.domain.valueobject;

import java.util.UUID;
import org.josiasguerrero.shared.domain.valueobject.EntityId;
import org.josiasguerrero.shared.domain.valueobject.UuidHelper;

public record ProductVariantId(UUID value) implements EntityId {
  public ProductVariantId {
    if (value == null) {
      throw new IllegalArgumentException("ProductVariantId cannot be null");
    }
  }

  public static ProductVariantId generate() {
    return new ProductVariantId(UuidHelper.generate());
  }

  public static ProductVariantId from(String raw) {
    return new ProductVariantId(UuidHelper.parse(raw));
  }

  public static ProductVariantId from(UUID uuid) {
    return new ProductVariantId(uuid);
  }
}
