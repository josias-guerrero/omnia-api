package org.josiasguerrero.products.application.mapper;

import org.josiasguerrero.products.application.dto.response.BrandResponse;
import org.josiasguerrero.products.domain.entity.Brand;

public class BrandMapper {

  public static BrandResponse toResponse(Brand brand) {
    return new BrandResponse(
        brand.getId().value(),
        brand.getName(),
        brand.getCreatedAt(),
        brand.getUpdatedAt());
  }
}
