package org.josiasguerrero.products.application.usecase.brand;

import org.josiasguerrero.products.application.dto.response.BrandResponse;
import org.josiasguerrero.products.application.mapper.BrandMapper;
import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.exception.BrandNotFoundException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;

public class FindBrandByIdUseCase {

  private BrandRepository brandRepository;

  public BrandResponse execute(BrandId brandId) {
    if (brandId == null) {
      throw new IllegalArgumentException("Brand id must not be null");
    }
    Brand brand = brandRepository.findById(brandId)
        .orElseThrow(() -> new BrandNotFoundException(brandId));
    return BrandMapper.toResponse(brand);
  }
}
