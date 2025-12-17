package org.josiasguerrero.products.application.usecase.brand;

import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteBrandUseCase {

  private BrandRepository brandRepository;

  public void execute(BrandId brandId) {
    brandRepository.delete(brandId);
  }
}
