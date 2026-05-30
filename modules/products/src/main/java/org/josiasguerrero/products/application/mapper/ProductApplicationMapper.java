package org.josiasguerrero.products.application.mapper;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.josiasguerrero.products.application.dto.response.BrandPResponse;
import org.josiasguerrero.products.application.dto.response.CategoryPResponse;
import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.dto.response.ProductVariantResponse;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.entity.ProductVariant;
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

    // Mapear todas las variantes
    Set<ProductVariantResponse> variants = product.getVariants().stream()
        .map(this::toVariantResponse)
        .collect(Collectors.toSet());

    // Construcción del DTO final
    return new ProductResponse(
        product.getId().value().toString(),
        product.getName(),
        product.getDescription(),
        brandDto,
        categories,
        variants,
        product.getCreatedAt(),
        product.getUpdatedAt());
  }

  private ProductVariantResponse toVariantResponse(ProductVariant variant) {
    Map<String, String> properties = variant.getProperties().stream()
        .map(attr -> propertyRepository.findById(attr.propertyId())
            .map(prop -> Map.entry(prop.getName(), attr.value().value())))
        .flatMap(Optional::stream)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

    return new ProductVariantResponse(
        variant.getId().value().toString(),
        variant.getSku().value(),
        variant.getBarcode() != null ? variant.getBarcode().value() : null,
        variant.getStock().quantity(),
        variant.getCost().amount(),
        variant.getPrice().amount(),
        properties,
        variant.getCreatedAt(),
        variant.getUpdatedAt()
    );
  }
}
