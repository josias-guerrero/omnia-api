package org.josiasguerrero.shared.domain.criteria;

import java.util.ArrayList;
import java.util.List;

import org.josiasguerrero.shared.domain.pagination.PageRequest;

public class CriteriaBuilder {

  private final List<Filter> filters = new ArrayList<>();
  private Order order;
  private PageRequest pageRequest = PageRequest.defaultRequest();

  public CriteriaBuilder filter(Filter filter) {
    this.filters.add(filter);
    return this;
  }

  public CriteriaBuilder filters(List<Filter> filters) {
    this.filters.addAll(filters);
    return this;
  }

  public CriteriaBuilder order(Order order) {
    this.order = order;
    return this;
  }

  public CriteriaBuilder pageRequest(PageRequest pageRequest) {
    this.pageRequest = pageRequest;
    return this;
  }

  public Criteria build() {
    return new Criteria(filters, order, pageRequest);
  }
}
