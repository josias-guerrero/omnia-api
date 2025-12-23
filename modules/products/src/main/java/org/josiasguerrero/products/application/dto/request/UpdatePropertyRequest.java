package org.josiasguerrero.products.application.dto.request;

import com.sun.istack.NotNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePropertyRequest(@NotNull Integer id, @NotBlank @Size(max = 50) String name) {
}
