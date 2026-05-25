package org.josiasguerrero.shared.domain.criteria;

public record Order(
    String field,
    OrderType type) {
  public static Order asc(String field) {
    return new Order(field, OrderType.ASC);
  }

  public static Order desc(String field) {
    return new Order(field, OrderType.DESC);
  }

  public static Order none() {
    return null;
  }
}
