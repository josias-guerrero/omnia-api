package org.josiasguerrero.products.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a custom property definition")
public record CreatePropertyRequest(
    @NotBlank @Size(max = 50) @Schema(description = "Property name", example = "color", maxLength = 50) String name) {
}
