package org.josiasguerrero.products.application.port;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.port.SkuGeneratorPort;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.springframework.stereotype.Component;

@Component
public class InternalSkuGenerator implements SkuGeneratorPort {

  @Override
  public Sku generateSku(Product product, Map<String, String> variantAttributes, String brandName) {
    List<String> parts = new ArrayList<>();

    String name = product.getName().trim();
    parts.add(name.length() >= 4 ? name.substring(0, 4).toUpperCase() : name.toUpperCase());

    if (brandName != null && !brandName.isBlank()) {
      parts.add(sanitize(brandName));
    }

    if (variantAttributes != null && !variantAttributes.isEmpty()) {
      variantAttributes.entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .map(e -> sanitize(e.getValue()))
          .filter(v -> !v.isEmpty())
          .forEach(parts::add);
    }

    return Sku.from(String.join("-", parts));
  }

  private String sanitize(String value) {
    if (value == null) return "";
    return value.trim()
        .toUpperCase()
        .replaceAll("[^A-Z0-9]", "-")
        .replaceAll("-+", "-")
        .replaceAll("^-|-$", "");
  }
}
