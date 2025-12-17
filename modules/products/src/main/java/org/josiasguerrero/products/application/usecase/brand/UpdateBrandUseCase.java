package org.josiasguerrero.products.application.usecase.brand;

import org.josiasguerrero.products.application.dto.request.UpdateBrandRequest;
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

  public void execute(UpdateBrandRequest request) {
    validator.validate(request);

    if (brandRepository.existsByName(request.name())) {
      throw new DuplicateBrandException(request.name());
    }

    BrandId brandId = BrandId.from(request.id());
    Brand brand = brandRepository.findById(brandId)
        .orElseThrow(() -> new BrandNotFoundException(brandId));

    brand.updateName(request.name());

    brandRepository.save(brand);

  }
}
