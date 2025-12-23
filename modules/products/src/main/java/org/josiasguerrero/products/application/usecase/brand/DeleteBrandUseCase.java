package org.josiasguerrero.products.application.usecase.brand;

import org.josiasguerrero.products.domain.port.BrandRepository;
import org.josiasguerrero.products.domain.valueobject.BrandId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteBrandUseCase {

  private BrandRepository brandRepository;

  public void execute(Integer brandId) {
    BrandId id = BrandId.from(brandId);

    if (brandRepository.hasProducts(id)) {
      throw new IllegalStateException("Cannot delete brand with associated products. "
          + "remove products from this brand first.");
    }

    brandRepository.delete(id);
  }
}
