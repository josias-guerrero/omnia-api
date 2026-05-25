package org.josiasguerrero.shared.domain.criteria;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record Filter(
    String field,
    FilterOperator operator,
    String value) {
  public static Filter equal(String field, String value) {
    return new Filter(field, FilterOperator.EQUAL, value);
  }

  public static Filter notEqual(String field, String value) {
    return new Filter(field, FilterOperator.NOT_EQUAL, value);
  }

  public static Filter greaterThan(String field, String value) {
    return new Filter(field, FilterOperator.GREATER_THAN, value);
  }

  public static Filter lessThan(String field, String value) {
    return new Filter(field, FilterOperator.LESS_THAN, value);
  }

  public static Filter contains(String field, String value) {
    return new Filter(field, FilterOperator.CONTAINS, value);
  }

  public static Filter notContains(String field, String value) {
    return new Filter(field, FilterOperator.NOT_CONTAINS, value);
  }

  public static Filter in(String field, String value) {
    return new Filter(field, FilterOperator.IN, value);
  }

  public static Filter memberOf(String field, String value) {
    return new Filter(field, FilterOperator.MEMBER_OF, value);
  }

  public static Filter anyIn(String field, List<?> values) {
    String joinedValues = values.stream()
        .map(Object::toString)
        .collect(Collectors.joining(""));

    return new Filter(field, FilterOperator.ANY_IN, joinedValues);
  }

  public List<String> getValueAsList() {
    if (value == null || value.isBlank()) {
      return List.of();
    }
    return Arrays.asList(value.split(","));
  }

  public List<Long> getValueAsLongList() {
    return getValueAsList().stream()
        .map(Long::parseLong)
        .collect(Collectors.toList());
  }

}
