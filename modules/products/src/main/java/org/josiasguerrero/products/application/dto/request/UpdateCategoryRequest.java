package org.josiasguerrero.products.application.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(@Size(max = 50) String name, @Size(max = 100) String description) {
}
