package org.josiasguerrero.products.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a brand")
public record BrandRequest(
    @NotBlank @Size(max = 50) @Schema(description = "Brand name", example = "Logitech", maxLength = 50) String name) {
}
