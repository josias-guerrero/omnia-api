package org.josiasguerrero.products.application;

import java.util.Map;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductOptionalData {
  private Integer brandId;
  private Set<Integer> categories;
  private Map<String, String> properties;
  private String barcode;
  private Integer stock;
}
