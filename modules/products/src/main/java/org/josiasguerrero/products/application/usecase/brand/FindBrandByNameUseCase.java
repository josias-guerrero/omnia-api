package org.josiasguerrero.products.application.usecase.brand;

import org.josiasguerrero.products.application.dto.response.BrandResponse;
import org.josiasguerrero.products.application.mapper.BrandMapper;
import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.exception.BrandNotFoundException;
import org.josiasguerrero.products.domain.port.BrandRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindBrandByNameUseCase {
  private BrandRepository brandRepository;

  public BrandResponse execute(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Brand id must not be null");
    }

    Brand brand = brandRepository.findByName(name)
        .orElseThrow(() -> new BrandNotFoundException(name));

    return BrandMapper.toResponse(brand);
  }
}
