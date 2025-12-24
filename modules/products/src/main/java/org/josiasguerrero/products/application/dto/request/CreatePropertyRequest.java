package org.josiasguerrero.products.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePropertyRequest(@NotBlank @Size(max = 50) String name) {
}
