package org.josiasguerrero.products.domain.port;

import java.util.Map;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.valueobject.Sku;

public interface SkuGeneratorPort {
  Sku generateSku(Product product, Map<String, String> variantAttributes, String brandName);
}
