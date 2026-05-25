package org.josiasguerrero.shared.domain.criteria;

import java.util.List;

import org.josiasguerrero.shared.domain.pagination.PageRequest;

public class Criteria {

  private final List<Filter> filters;
  private final Order order;
  private final PageRequest pageRequest;

  public Criteria(List<Filter> filters, Order order, PageRequest pageRequest) {
    this.filters = filters;
    this.order = order;
    this.pageRequest = pageRequest;
  }

  public static CriteriaBuilder builder() {
    return new CriteriaBuilder();
  }

  public boolean hasFilters() {
    return !filters.isEmpty();
  }

  public List<Filter> getFilters() {
    return filters;
  }

  public Order getOrder() {
    return order;
  }

  public PageRequest getPageRequest() {
    return pageRequest;
  }

}
