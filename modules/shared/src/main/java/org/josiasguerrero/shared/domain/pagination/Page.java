package org.josiasguerrero.shared.domain.pagination;

import java.util.List;
import java.util.function.Function;

public record Page<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages) {
  public Page {
    if (content == null) {
      throw new IllegalArgumentException("The content cannot be null");
    }
    if (page < 0) {
      throw new IllegalArgumentException("Page number cannot be negative");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Page size must be higher than 0");
    }
    if (totalElements < 0) {
      throw new IllegalArgumentException("Total elements cannot be negative");
    }
    if (totalPages < 0) {
      throw new IllegalArgumentException("Total pages cannot be negative");
    }
  }

  public static <T> Page<T> empty(PageRequest pageRequest) {
    return new Page<>(List.of(), pageRequest.page(), pageRequest.size(), 0, 0);
  }

  public static <T> Page<T> of(List<T> content, PageRequest pageRequest, long totalElements) {
    int totalPages = (int) Math.ceil((double) totalElements / pageRequest.size());
    return new Page<>(content, pageRequest.page(), pageRequest.size(), totalElements, totalPages);
  }

  public boolean hasNext() {
    return page < totalPages - 1;
  }

  public boolean hasPrevious() {
    return page > 0;
  }

  public boolean isFirst() {
    return page == 0;
  }

  public boolean isEmpty() {
    return content.isEmpty();
  }

  public <R> Page<R> map(Function<T, R> mapper) {
    List<R> mappedContent = content.stream()
        .map(mapper)
        .toList();
    return new Page<>(mappedContent, page, size, totalElements, totalPages);
  }
}
