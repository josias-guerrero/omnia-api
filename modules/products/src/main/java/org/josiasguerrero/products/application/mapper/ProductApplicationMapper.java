package org.josiasguerrero.products.application.mapper;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.josiasguerrero.products.application.dto.response.BrandPResponse;
import org.josiasguerrero.products.application.dto.response.CategoryPResponse;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.port.CategoryRepository;
import org.josiasguerrero.products.domain.port.PropertyRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductApplicationMapper {

  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final PropertyRepository propertyRepository;

  public ProductResponse toResponse(Product product) {
    // Marca
    BrandPResponse brandDto = Optional.ofNullable(product.getBrandId())
        .flatMap(id -> brandRepository.findById(id)
            .map(b -> new BrandPResponse(b.getId().value(), b.getName())))
        .orElse(null);

    // Categorías
    Set<CategoryPResponse> categories = product.getCategoryIds().stream()
        .map(catId -> categoryRepository.findById(catId)
            .map(cat -> new CategoryPResponse(cat.getId().value(), cat.getName())))
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

    // Obtener la primera variante como por defecto
    var firstVariant = product.getVariants().stream().findFirst();

    // Propiedades mapeadas de la primera variante
    Map<String, String> properties = firstVariant
        .map(v -> v.getProperties().stream()
            .map(attr -> propertyRepository.findById(attr.propertyId())
                .map(prop -> Map.entry(prop.getName(), attr.value().value())))
            .flatMap(Optional::stream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a)))
        .orElse(Map.of());

    // Barcode y otros campos seguros de la primera variante
    String sku = firstVariant.map(v -> v.getSku().value()).orElse(null);
    String barcode = firstVariant.flatMap(v -> Optional.ofNullable(v.getBarcode()).map(b -> b.value())).orElse(null);
    java.math.BigDecimal cost = firstVariant.map(v -> v.getCost().amount()).orElse(java.math.BigDecimal.ZERO);
    java.math.BigDecimal price = firstVariant.map(v -> v.getPrice().amount()).orElse(java.math.BigDecimal.ZERO);
    Integer stock = firstVariant.map(v -> v.getStock().quantity()).orElse(0);

    // Construcción del DTO final
    return new ProductResponse(
        product.getId().value().toString(),
        sku,
        product.getName(),
        product.getDescription(),
        barcode,
        cost,
        price,
        stock,
        brandDto,
        categories,
        properties,
        product.getCreatedAt(),
        product.getUpdatedAt());
  }
}
