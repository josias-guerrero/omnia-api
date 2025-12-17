package org.josiasguerrero.products.application.dto.request;

import com.sun.istack.NotNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBrandRequest(@NotNull Integer id, @NotBlank @Size(max = 100) String name) {
}
