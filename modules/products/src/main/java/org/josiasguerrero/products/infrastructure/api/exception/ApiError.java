package org.josiasguerrero.products.infrastructure.api.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private Map<String, String> details;
}
