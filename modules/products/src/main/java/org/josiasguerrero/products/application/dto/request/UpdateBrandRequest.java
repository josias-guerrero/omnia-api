package org.josiasguerrero.products.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating a brand")
public record UpdateBrandRequest(
    @NotBlank @Size(max = 100) @Schema(description = "Brand name", example = "Logitech", maxLength = 100) String name) {
}
