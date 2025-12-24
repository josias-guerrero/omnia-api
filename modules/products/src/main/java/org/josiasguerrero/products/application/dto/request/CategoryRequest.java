package org.josiasguerrero.products.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(@NotBlank @Size(max = 50) String name, @Size(max = 100) String description) {
}
