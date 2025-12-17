package org.josiasguerrero.products.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(@NotNull Integer id, String name, String description) {
}
