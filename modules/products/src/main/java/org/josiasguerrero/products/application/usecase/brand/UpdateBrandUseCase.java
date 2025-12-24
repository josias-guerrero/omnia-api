package org.josiasguerrero.products.application.usecase.brand;

import org.josiasguerrero.products.application.dto.request.UpdateBrandRequest;
import org.josiasguerrero.products.application.dto.response.BrandResponse;
import org.josiasguerrero.products.application.mapper.BrandMapper;
import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.exception.BrandNotFoundException;
import org.josiasguerrero.products.domain.exception.DuplicateBrandException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateBrandUseCase {

  private BrandRepository brandRepository;
  private DtoValidator validator;

  public BrandResponse execute(Integer brandId, UpdateBrandRequest request) {
    validator.validate(request);
    BrandId id = BrandId.from(brandId);

    if (brandRepository.existsByName(request.name())) {
      throw new DuplicateBrandException(request.name());
    }

    Brand brand = brandRepository.findById(id)
        .orElseThrow(() -> new BrandNotFoundException(id));

    brand.updateName(request.name());

    brandRepository.save(brand);

    return BrandMapper.toResponse(brand);
  }
}
