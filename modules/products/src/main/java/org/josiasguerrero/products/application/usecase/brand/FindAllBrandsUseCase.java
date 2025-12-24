package org.josiasguerrero.products.application.usecase.brand;

import java.util.List;

import org.josiasguerrero.products.application.dto.response.BrandResponse;
import org.josiasguerrero.products.application.mapper.BrandMapper;
import org.josiasguerrero.products.domain.port.BrandRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindAllBrandsUseCase {

  private BrandRepository brandRepository;

  public List<BrandResponse> execute() {
    return brandRepository.findAll().stream().map(BrandMapper::toResponse).toList();
  }
}
