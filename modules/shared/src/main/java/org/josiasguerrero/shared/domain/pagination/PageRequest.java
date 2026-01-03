package org.josiasguerrero.shared.domain.pagination;

/**
 * Value object that represents a page request
 **/
public record PageRequest(int page, int size) {
  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 20;
  private static final int MAX_SIZE = 100;

  public PageRequest {
    if (page < 0) {
      throw new IllegalArgumentException("Page number cannot be negative");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Page size must be higher than 0");
    }
    if (size > MAX_SIZE) {
      throw new IllegalArgumentException("Page size cannot exceed " + MAX_SIZE);
    }
  }

  public static PageRequest of(int page, int size) {
    return new PageRequest(page, size);
  }

  public static PageRequest ofPage(int page) {
    return new PageRequest(page, DEFAULT_SIZE);
  }

  public static PageRequest defaultRequest() {
    return new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE);
  }

  public int offset() {
    return page * size;
  }

  public PageRequest next() {
    return new PageRequest(page + 1, size);
  }

  public PageRequest previous() {
    return page == 0 ? this : new PageRequest(page - 1, size);
  }

}
