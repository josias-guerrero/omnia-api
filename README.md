# Omnia API

<!--toc:start-->

- [Omnia API](#omnia-api)
  - [What Is This?](#what-is-this)
  - [Tech Stack](#tech-stack)
  - [Architecture](#architecture)
  - [Running Locally](#running-locally)
    - [Prerequisites](#prerequisites)
    - [1. Start the infrastructure](#1-start-the-infrastructure)
    - [2. Configure environment (optional)](#2-configure-environment-optional)
    - [3. Run the API](#3-run-the-api)
    - [4. Run tests](#4-run-tests)
  - [API Overview](#api-overview)
  - [Roadmap](#roadmap)
    - [✅ v0.1 — Products Foundation _(current)_](#v0-1-products-foundation-current)
    - [🔲 v0.2 — Inventory & Warehouses](#🔲-v02-inventory-warehouses)
    - [🔲 v0.3 — Sales & Orders](#🔲-v03-sales-orders)
    - [🔲 v0.4 — Customers](#🔲-v04-customers)
    - [🔲 v0.5 — Reports & Finance](#🔲-v05-reports-finance)
    - [🔲 v1.0 — Production Ready](#🔲-v10-production-ready)
  - [Project Decisions](#project-decisions)
  - [Module Documentation](#module-documentation)
  - [Deployment](#deployment)
  <!--toc:end-->

> A modular, ERP-oriented backend API built for real-world business operations.

---

## What Is This?

Omnia API is a **RESTful backend** for managing the core operations of a retail
or small business:
products, inventory, sales orders, customers, and finance — all in a single,
self-hostable system.

The project follows a **modular architecture** where each business domain
(products, inventory, sales, etc.)
is an independent Gradle submodule with its own domain model, use cases, and API.
Modules share nothing
except a common **shared kernel** with reusable primitives.

**Current stage:** Foundation — Products module fully implemented.

---

## Tech Stack

| Layer             | Technology                            |
| ----------------- | ------------------------------------- |
| Language          | Java 25                               |
| Framework         | Spring Boot 4.0.0                     |
| Database          | PostgreSQL 15                         |
| Schema migrations | Flyway                                |
| ORM               | Spring Data JPA + Hibernate           |
| Caching           | Redis 7 (provisioned, not yet active) |
| Validation        | Jakarta Validation                    |
| Build             | Gradle (multi-project)                |
| Containerization  | Docker + Docker Compose               |
| CI/CD             | GitHub Actions → GHCR → SSH deploy    |

---

## Architecture

The project is a **multi-module Gradle build** where each module is a self-contained
[Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/) implementation.

```text
omnia-api/
├── src/                        # Root bootstrap (main class, global config)
├── modules/
│   ├── shared/                 # Shared kernel: primitives used by all modules
│   ├── products/               # Products bounded context
│   ├── inventory/              # (planned) Warehouses & stock movements
│   ├── sales/                  # (planned) Orders & fulfillment
│   └── customers/              # (planned) Customer management
├── Dockerfile
├── docker-compose.yml
└── .github/workflows/deploy.yml
```

Each module follows the same internal structure:

```text
module/
└── src/main/java/org/josiasguerrero/<module>/
    ├── domain/          # Entities, value objects, domain exceptions,
                          repository ports
    ├── application/     # Use cases, DTOs, mappers, queries
    └── infrastructure/  # JPA repos, REST controllers, Spring configuration
```

The **domain layer has zero framework dependencies**. Infrastructure adapts to
the domain — never the other way around.

---

## Running Locally

### Prerequisites

- Java 25 (Temurin recommended)
- Docker + Docker Compose

### 1. Start the infrastructure

```bash
docker compose up -d
```

This starts PostgreSQL 15 (port 5432) and Redis 7 (port 6380).

### 2. Configure environment (optional)

The app uses sane defaults for local development. Override via environment
variables if needed:

| Variable          | Default                 | Description         |
| ----------------- | ----------------------- | ------------------- |
| `DB_PORT`         | `5432`                  | PostgreSQL port     |
| `DB`              | `omniadb`               | Database name       |
| `DB_USERNAME`     | `omniauser`             | DB user             |
| `DB_PASSWORD`     | `omniapass`             | DB password         |
| `REDIS_PORT`      | `6380`                  | Redis port          |
| `ALLOWED_ORIGINS` | `http://localhost:3000` | CORS allowed origin |

### 3. Run the API

```bash
./gradlew bootRun
```

The API starts on `http://localhost:8080`.  
Flyway automatically runs all pending migrations on startup.

### 4. Run tests

```bash
./gradlew test
```

---

## API Overview

Base path: `/api/v1`

| Module       | Base Route           | Status     |
| ------------ | -------------------- | ---------- |
| Products     | `/api/v1/products`   | ✅ Active  |
| Brands       | `/api/v1/brands`     | ✅ Active  |
| Categories   | `/api/v1/categories` | ✅ Active  |
| Properties   | `/api/v1/properties` | ✅ Active  |
| Inventory    | `/api/v1/inventory`  | 🔲 Planned |
| Sales Orders | `/api/v1/orders`     | 🔲 Planned |
| Customers    | `/api/v1/customers`  | 🔲 Planned |

See each module's `README.md` for detailed endpoint documentation.

---

## Roadmap

### ✅ v0.1 — Products Foundation _(current)_

- Products CRUD with dynamic search and filtering
- Brands, Categories, Properties management
- Multi-module Gradle project structure
- Docker + CI/CD pipeline

### 🔲 v0.2 — Inventory & Warehouses

- Warehouse management (create, list, activate/deactivate)
- Stock movement tracking (entries, exits, adjustments, transfers)
- Stock per warehouse per product
- Low-stock alerts

### 🔲 v0.3 — Sales & Orders

- Sales order lifecycle: Draft → Confirmed → Shipped → Delivered
- Stock reservation on order confirmation
- Order number generation (ORD-YYYY-NNNN)
- Order search with filters

### 🔲 v0.4 — Customers

- Customer profiles (name, email, phone, address, tax ID)
- Customer linking to sales orders

### 🔲 v0.5 — Reports & Finance

- Revenue summary by date range
- Inventory valuation (stock × cost)
- Low-stock report
- Sales trends

### 🔲 v1.0 — Production Ready

- Authentication & authorization (JWT)
- Full test coverage (unit + integration)
- OpenAPI / Swagger documentation
- Rate limiting and request tracing

---

## Project Decisions

Key architectural decisions are documented in [`docs/adr/`](docs/adr/).

| ADR                                                   | Decision                       |
| ----------------------------------------------------- | ------------------------------ |
| [ADR-001](docs/adr/ADR-001-hexagonal-architecture.md) | Use Hexagonal Architecture     |
| [ADR-002](docs/adr/ADR-002-gradle-multimodule.md)     | Use Gradle multi-module layout |

---

## Module Documentation

- [`modules/shared`](modules/shared/README.md) — Shared kernel: primitives,
  criteria, pagination
- [`modules/products`](modules/products/README.md) — Products, brands,
  categories, properties

---

## Deployment

The CI/CD pipeline (GitHub Actions) builds a multi-architecture Docker image and
pushes it to GHCR on every push to `main`.

To deploy manually:

```bash
docker compose pull backend
docker compose up -d backend
```

See `.github/workflows/deploy.yml` for the full pipeline definition.
