package org.josiasguerrero.products.domain.port;

import java.util.List;

import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.products.domain.valueobject.CategoryId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;

public interface SkuGeneratorPort {

  String generateSku(Product product, BrandId brand, List<PropertyId> properties, List<CategoryId> categories);
}
