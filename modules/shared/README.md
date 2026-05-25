# Shared Kernel (`modules/shared`)

The shared kernel provides **foundational primitives** used across all Omnia modules.
It has no business logic of its own — it only contains building blocks.

> **Rule:** Only truly cross-cutting, stable abstractions belong here.  
> If something is specific to one module, it stays in that module.

---

## Contents

### Value Objects (`domain/valueobject/`)

#### `Money`
Represents a monetary amount with currency. Wraps `BigDecimal` to avoid floating-point precision issues.

```java
Money price = new Money(29.99);           // defaults to USD
Money cost  = new Money(BigDecimal.valueOf(15.00), Currency.getInstance("USD"));

price.isGreaterThan(cost);  // true
price.add(cost);            // Money(44.99, USD)
price.multiply(BigDecimal.valueOf(2));
Money.zero();               // Money(0.00, USD)
```

Rules enforced:
- Amount cannot be negative
- Cannot operate (add, subtract, compare) across different currencies

#### `EntityId`
Abstract base for UUID-backed entity identifiers. Extend it to create typed IDs:

```java
public record ProductId(UUID value) implements EntityId { }
```

#### `UuidHelper`
Generates new UUIDs:

```java
UUID id = UuidHelper.generate();
```

---

### Pagination (`domain/pagination/`)

Domain-owned pagination — completely independent of Spring's `Pageable`.

#### `PageRequest`
```java
PageRequest request = PageRequest.of(0, 20);  // page 0, 20 items
request.offset();  // 0
request.size();    // 20
```

#### `Page<T>`
```java
Page<Product> page = Page.of(products, pageRequest, totalCount);

page.content();        // List<Product>
page.totalElements();  // long
page.totalPages();     // int
page.hasNext();        // boolean
page.hasPrevious();    // boolean
page.isEmpty();        // boolean

// Map content without changing pagination metadata
Page<ProductResponse> responsePage = page.map(mapper::toResponse);

// Empty page (e.g., no results found)
Page<Product> empty = Page.empty(pageRequest);
```

---

### Criteria & Filtering (`domain/criteria/`)

A flexible, type-safe specification pattern for building dynamic queries.
Use it in your query handlers to build filters without writing raw queries.

#### `Filter`
```java
// Basic filters
Filter.equal("name", "Laptop");
Filter.contains("name", "lap");         // LIKE %lap%
Filter.lessThan("stock", "10");
Filter.greaterThan("price", "100");

// Collections
Filter.anyIn("categories", List.of(1L, 2L, 3L));  // product is in ANY of these categories
Filter.in("status", "ACTIVE,INACTIVE");             // field IN (values)
```

Available operators (`FilterOperator`):

| Operator | SQL Equivalent |
|---|---|
| `EQUAL` | `field = value` |
| `NOT_EQUAL` | `field != value` |
| `CONTAINS` | `LOWER(field) LIKE '%value%'` |
| `NOT_CONTAINS` | `NOT LIKE` |
| `GREATER_THAN` | `field > value` |
| `GREATER_THAN_OR_EQUAL` | `field >= value` |
| `LESS_THAN` | `field < value` |
| `LESS_THAN_OR_EQUAL` | `field <= value` |
| `IN` | `field IN (...)` |
| `NOT_IN` | `field NOT IN (...)` |
| `ANY_IN` | Join + `id IN (...)` (many-to-many) |
| `MEMBER_OF` | Join + `id = value` (many-to-many) |

#### `Order`
```java
Order.asc("name");
Order.desc("createdAt");
```

#### `Criteria` + `CriteriaBuilder`
```java
Criteria criteria = Criteria.builder()
    .filter(Filter.contains("name", searchTerm))
    .filter(Filter.equal("brandId.id", String.valueOf(brandId)))
    .filter(Filter.lessThan("stock", String.valueOf(threshold)))
    .order(Order.desc("createdAt"))
    .pageRequest(PageRequest.of(page, size))
    .build();

Page<Product> results = productRepository.findByCriteria(criteria);
```

---

### Validation (`application/validation/`)

#### `DtoValidator`
Triggers Jakarta Bean Validation programmatically inside use cases (not just at the controller layer):

```java
// In a use case constructor or execute method:
dtoValidator.validate(request);  // throws ValidationException if invalid
```

---

## Dependencies

The shared module has **minimal dependencies** on purpose:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-validation'
}
```

It does **not** depend on Spring Data JPA, Spring Web, or any other module.

---

## Adding New Shared Primitives

Before adding anything to `shared`, ask:

1. Is it used by **more than one module**?
2. Is it **stable** (unlikely to change with business rules)?
3. Is it **purely technical** (not tied to a specific domain)?

If all three are yes → add it here. Otherwise, keep it in its module.
