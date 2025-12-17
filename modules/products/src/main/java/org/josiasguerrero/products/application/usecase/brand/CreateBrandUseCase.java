package org.josiasguerrero.products.application.usecase.brand;

import org.josiasguerrero.products.application.dto.request.BrandRequest;
import org.josiasguerrero.products.application.dto.response.BrandResponse;
import org.josiasguerrero.products.application.mapper.BrandMapper;
import org.josiasguerrero.products.domain.entity.Brand;
import org.josiasguerrero.products.domain.exception.DuplicateBrandException;
import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateBrandUseCase {

  private BrandRepository brandRepository;
  private DtoValidator validator;

  public BrandResponse execute(BrandRequest request) {
    validator.validate(request);
    if (brandRepository.existsByName(request.name())) {
      throw new DuplicateBrandException(request.name());
    }

    Brand brand = new Brand(request.name());

    brandRepository.save(brand);

    return BrandMapper.toResponse(brand);

  }
}
