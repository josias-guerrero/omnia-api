# Products Module (`modules/products`)

<!--toc:start-->

- [Products Module (`modules/products`)](#products-module-modulesproducts)
  - [Responsibility](#responsibility)
  - [Domain Model](#domain-model)
    - [Business Rules](#business-rules)
  - [API Reference](#api-reference)
    - [Products — `/api/v1/products`](#products-apiv1products)
      - [Search Parameters (`GET /api/v1/products`)](#search-parameters-get-apiv1products)
      - [Create Product Request Body](#create-product-request-body)
    - [Brands — `/api/v1/brands`](#brands-apiv1brands)
    - [Categories — `/api/v1/categories`](#categories-apiv1categories)
    - [Properties — `/api/v1/properties`](#properties-apiv1properties)
  - [Error Responses](#error-responses)
  - [Architecture Layers](#architecture-layers)
    - [Layer Rules](#layer-rules)
  - [Database Schema](#database-schema)
  - [Adding a New Use Case](#adding-a-new-use-case)
  <!--toc:end-->

Manages the product catalog for Omnia. This is the core reference domain —
all other modules (inventory, sales) depend on products existing.

---

## Responsibility

This module owns:

- **Products** — the sellable items in your catalog
- **Brands** — manufacturers or brand labels assigned to products
- **Categories** — organizational groupings for products (many-to-many)
- **Properties** — custom key-value attributes attached to products (e.g.,
  color, size, material)

It does **not** own:

- Stock movements or warehouse stock → that belongs to `inventory`
- Sales or orders → that belongs to `sales`

---

## Domain Model

```text
Product (UUID)
  ├── sku: Sku                         unique identifier per product
  ├── name: String                     max 100 chars
  ├── description: String              optional
  ├── barcode: Barcode                 optional, unique
  ├── cost: Money                      purchase cost (must be < price)
  ├── price: Money                     selling price (must be > cost)
  ├── stock: Stock                     non-negative integer
  ├── brandId: BrandId                 optional FK to Brand
  ├── categoryIds: Set<CategoryId>     many-to-many
  └── properties: Map<PropertyId, PropertyValue>   key-value attributes

Brand (Integer)
  └── name: String   unique

Category (Integer)
  ├── name: String
  └── description: String

Property (Integer)
  └── name: String   unique   (e.g. "Color", "Material", "Weight")
```

### Business Rules

- `price` must always be greater than `cost`
- `sku` must be unique across all products
- `barcode`, when set, must be unique across all products
- `stock` cannot go below zero (enforced in the `Stock` value object)
- Product `name` cannot be blank and cannot exceed 100 characters
- `Brand`, `Category`, and `Property` names must be unique

---

## API Reference

### Products — `/api/v1/products`

| Method   | Path                               | Description                         |
| -------- | ---------------------------------- | ----------------------------------- |
| `POST`   | `/api/v1/products`                 | Create a new product                |
| `GET`    | `/api/v1/products/{id}`            | Get product by UUID                 |
| `GET`    | `/api/v1/products`                 | Search/filter products (paginated)  |
| `PUT`    | `/api/v1/products/{id}`            | Update product fields               |
| `DELETE` | `/api/v1/products/{id}`            | Delete product                      |
| `POST`   | `/api/v1/products/{id}/categories` | Replace all categories on a product |
| `POST`   | `/api/v1/products/{id}/properties` | Replace all properties on a product |

#### Search Parameters (`GET /api/v1/products`)

| Param               | Type         | Description                                      |
| ------------------- | ------------ | ------------------------------------------------ |
| `search`            | `String`     | Filter by name (case-insensitive contains)       |
| `brandId`           | `Long`       | Filter by brand                                  |
| `categoryIds`       | `List<Long>` | Filter by one or more categories                 |
| `lowStockThreshold` | `Integer`    | Return only products with stock below this value |
| `page`              | `int`        | Page number (default: `0`)                       |
| `size`              | `int`        | Page size (default: `20`)                        |

#### Create Product Request Body

```json
{
  "sku": "LAPTOP-001",
  "name": "MacBook Pro 14",
  "description": "Apple laptop with M3 chip",
  "cost": 1200.0,
  "price": 1599.99,
  "brandId": 1,
  "categoryIds": [2, 5],
  "properties": {
    "1": "Space Gray",
    "3": "512GB"
  }
}
```

> Properties keys are `Property` IDs (integers), values are free-text strings.

---

### Brands — `/api/v1/brands`

| Method   | Path                         | Description       |
| -------- | ---------------------------- | ----------------- |
| `POST`   | `/api/v1/brands`             | Create brand      |
| `GET`    | `/api/v1/brands`             | List all brands   |
| `GET`    | `/api/v1/brands/{id}`        | Get brand by ID   |
| `GET`    | `/api/v1/brands/name/{name}` | Get brand by name |
| `PUT`    | `/api/v1/brands/{id}`        | Update brand      |
| `DELETE` | `/api/v1/brands/{id}`        | Delete brand      |

---

### Categories — `/api/v1/categories`

| Method   | Path                      | Description         |
| -------- | ------------------------- | ------------------- |
| `POST`   | `/api/v1/categories`      | Create category     |
| `GET`    | `/api/v1/categories`      | List all categories |
| `GET`    | `/api/v1/categories/{id}` | Get category by ID  |
| `PUT`    | `/api/v1/categories/{id}` | Update category     |
| `DELETE` | `/api/v1/categories/{id}` | Delete category     |

---

### Properties — `/api/v1/properties`

Properties are **attribute definitions** (e.g., "Color", "Size"). Values are set
per product via the product API.

| Method   | Path                      | Description                   |
| -------- | ------------------------- | ----------------------------- |
| `POST`   | `/api/v1/properties`      | Create property definition    |
| `GET`    | `/api/v1/properties`      | List all property definitions |
| `GET`    | `/api/v1/properties/{id}` | Get property by ID            |
| `PUT`    | `/api/v1/properties/{id}` | Update property               |
| `DELETE` | `/api/v1/properties/{id}` | Delete property               |

---

## Error Responses

All errors follow a consistent structure:

```json
{
  "timestamp": "2026-05-24T21:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: abc-123",
  "details": {}
}
```

| HTTP Status                 | When                                                 |
| --------------------------- | ---------------------------------------------------- |
| `400 Bad Request`           | Validation failure, business rule violation          |
| `404 Not Found`             | Product, brand, category, or property doesn't exist  |
| `409 Conflict`              | Duplicate SKU, barcode, brand name, or property name |
| `500 Internal Server Error` | Unexpected server error                              |

---

## Architecture Layers

```text
products/
└── src/main/java/org/josiasguerrero/products/
    │
    ├── domain/                          Pure Java — no framework dependencies
    │   ├── entity/
    │   │   ├── Product.java             Aggregate root with business methods
    │   │   ├── Brand.java
    │   │   ├── Category.java
    │   │   └── Property.java
    │   ├── valueobject/
    │   │   ├── Sku.java                 Validates format
    │   │   ├── Barcode.java
    │   │   ├── Stock.java               Guards against negative stock
    │   │   ├── ProductId.java           UUID wrapper
    │   │   ├── BrandId.java
    │   │   ├── CategoryId.java
    │   │   ├── PropertyId.java
    │   │   └── PropertyValue.java
    │   ├── port/                        Interfaces — implemented by infrastructure
    │   │   ├── ProductRepository.java
    │   │   ├── BrandRepository.java
    │   │   ├── CategoryRepository.java
    │   │   └── PropertyRepository.java
    │   └── exception/                   Domain-specific exceptions
    │
    ├── application/                     Orchestrates domain — no HTTP or JPA
    │   ├── dto/
    │   │   ├── request/                 Input DTOs (validated)
    │   │   └── response/                Output DTOs
    │   ├── mapper/                      Domain ↔ DTO conversion
    │   ├── query/                       Query objects (read side)
    │   └── usecase/                     One class per operation
    │       ├── Product/
    │       ├── brand/
    │       ├── category/
    │       └── property/
    │
    └── infrastructure/                  Spring + JPA + HTTP — adapts to domain
        ├── api/
        │   ├── controller/              REST controllers
        │   └── exception/              GlobalExceptionHandler + ApiError
        ├── configuration/
        │   └── UseCaseConfiguration.java   Wires use cases as Spring beans
        └── persistence/
            ├── entity/                 JPA entities (separate from domain entities)
            ├── mapper/                 JPA entity ↔ Domain entity
            └── repository/            JPA repos + domain port implementations
```

### Layer Rules

| Layer            | Can depend on                                  | Cannot depend on                             |
| ---------------- | ---------------------------------------------- | -------------------------------------------- |
| `domain`         | `shared` kernel only                           | `application`, `infrastructure`, Spring, JPA |
| `application`    | `domain`, `shared`                             | `infrastructure`, Spring MVC, JPA            |
| `infrastructure` | `application`, `domain`, `shared`, Spring, JPA | Nothing extra                                |

---

## Database Schema

```sql
products          (id UUID, sku, name, description, barcode, cost, price, stock, brand_id)
brands            (id INTEGER, name UNIQUE)
categories        (id INTEGER, name, description)
properties        (id INTEGER, name UNIQUE)
product_category  (product_id UUID, category_id INTEGER)   -- join table
product_property  (id BIGINT, product_id UUID, property_id INTEGER, value VARCHAR)
```

Migration file: `src/main/resources/db/migration/V2__PRODUCTS_RELATED_TABLES.sql`

---

## Adding a New Use Case

Follow this checklist when adding new functionality:

1. **Domain** — Does it require a new method on an entity? Add it there with
   business validation.
2. **Port** — Does it need a new repository query? Add the method to the port
   interface in `domain/port/`.
3. **Application** — Create a new use case class in `application/usecase/`.
   Keep it focused on one operation.
4. **Infrastructure** — Implement the new port method in
   `persistence/repository/`. Add the Spring `@Bean` to `UseCaseConfiguration`.
5. **Controller** — Add the endpoint to the appropriate controller.
6. **Test** — Write a unit test for the use case (mock the repository) and
   ideally an integration test for the endpoint.
