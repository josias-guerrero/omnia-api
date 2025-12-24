package org.josiasguerrero.products.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBrandRequest(@NotBlank @Size(max = 100) String name) {
}
